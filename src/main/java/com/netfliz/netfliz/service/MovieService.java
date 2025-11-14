package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MoviesApiDelegate;
import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.mapper.MovieMapper;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.MovieByGenreDto;
import com.netfliz.netfliz.model.MoviePage;
import com.netfliz.netfliz.model.request.MovieByGenreRequest;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import com.netfliz.netfliz.model.response.MovieByGenreResponse;
import com.netfliz.netfliz.repository.CustomMovieRepository;
import com.netfliz.netfliz.repository.IMovieRepository;
import com.netfliz.netfliz.validator.MovieValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService implements MoviesApiDelegate {
    IMovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieValidator movieValidator;
    private final CustomMovieRepository customMovieRepository;
    private final RedisService redisService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MoviePage> getAllMovie(Integer page, Integer pageSize, String filter, String sort) {
        Specification<MovieEntity> specification = null;
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (filter != null && !filter.isEmpty()) {
            String[] filterArray = filter.split(" ");
            String field =  Arrays.stream(filterArray).findFirst().get();
            String operator = Arrays.stream(filterArray).skip(1).findFirst().get();
            String value = Arrays.stream(filterArray).skip(2).findFirst().get();

            specification = (root, query, criteriaBuilder) -> {
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(field), value)
                );
            };

            Page<MovieEntity> resultPage = movieRepository.findAllByFieldName(field, value, pageable);
            return ResponseEntity.ok(buildPage(resultPage));
        }

        Page<MovieEntity> resultPage = movieRepository.findAll(pageable);
        return ResponseEntity.ok(buildPage(resultPage));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> getMovieById(Long movieId) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = movieMapper.mapMovieEntityToMovie(movieRepository.findById(movieId).get());
        return ResponseEntity.ok(movie);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> createMovie(Movie movie) {
        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieRepository.save(movieEntity);
        return ResponseEntity.ok(movie);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMovie(Long movieId, Movie movie) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieEntity.setId(movieId);
        movieRepository.save(movieEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(Long movieId) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        movieRepository.deleteById(movieId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Movie>> bulkMovie(List<Movie> movies) {
        List<MovieEntity> entityList = movies.stream().map(movieMapper::mapMovieToMovieEntity).toList();
        movieRepository.saveAll(entityList);

        return ResponseEntity.ok(movies);
    }

    /**
     * Lấy phim theo thể loại, dùng trên màn hình chính
     * Không phân quyền
     */
    public ResponseEntity<List<MovieByGenreResponse>> getMoviesByGenres(MovieByGenreRequest request) {
        request.validate();
        String genreKey = CacheKey.buildKey(request.getGenres());
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_MOVIE_BY_GENRES, genreKey, String.valueOf(request.getLimit()));
        var cachedMovieByGenreList = redisService.getList(cacheKey, MovieByGenreResponse.class);

        if (Objects.nonNull(cachedMovieByGenreList)) {
            return ResponseEntity.ok(cachedMovieByGenreList);
        }

        List<MovieByGenreDto> listDto = customMovieRepository.findByGenres(request.getGenres(), request.getLimit());
        Map<String, List<MovieByGenreDto>> map = listDto.stream().collect(Collectors.groupingBy(MovieByGenreDto::getName));
        List<MovieByGenreResponse> responses = new ArrayList<>();

        map.forEach((key, value) -> {
            MovieByGenreResponse response = new MovieByGenreResponse();
            response.setGenre(key);
            response.setMovies(movieMapper.mapMovieByGenreDtoToMovieList(value));
            responses.add(response);
        });

        // Cache
        redisService.set(cacheKey, responses);

        return ResponseEntity.ok(responses);
    }

    /**
     * Lấy phim theo filter, dùng trên màn lọc phim
     * Không phân quyền
     */
    public ResponseEntity<MoviePage> getMoviesByFilter(MovieFilterRequest request) {
        request.validate();

        Page<MovieEntity> resultPage = customMovieRepository.findByFilter(request);
        return ResponseEntity.ok(buildPage(resultPage));
    }

    public String setFilterQuery(String filter) {
        String query = "";

        if (filter != null && !filter.isEmpty()) {
            String[] filterArray = filter.split(" ");
            String operator = Arrays.stream(filterArray).skip(1).findFirst().get();
            String value = Arrays.stream(filterArray).skip(2).findFirst().get();

            query = switch (operator) {
                case "ne" -> " != " + value;
                case "in" ->" like " + "%" + value + "%";
                case "nin" ->" not like " + "%" + value + "%";
                default -> " = " + value;
            };
        }

        return query;
    }

    public MoviePage buildPage(Page<MovieEntity> resultPage) {
        MoviePage moviePage = new MoviePage();

        moviePage.setPage(resultPage.getNumber() + 1);
        moviePage.setPageSize(resultPage.getSize());
        moviePage.setTotalPages(resultPage.getTotalPages());
        moviePage.setTotal((int) resultPage.getTotalElements());
        moviePage.setItems(movieMapper.mapMovieEntityListToMovieList(resultPage.getContent()));

        return moviePage;
    }
}
