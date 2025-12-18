package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MoviesApiDelegate;
import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieAssetType;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import com.netfliz.netfliz.entity.enums.MovieObjectType;
import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.mapper.MovieAssetMapper;
import com.netfliz.netfliz.mapper.MovieImageMapper;
import com.netfliz.netfliz.mapper.MovieMapper;
import com.netfliz.netfliz.model.*;
import com.netfliz.netfliz.model.request.MovieByGenreRequest;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import com.netfliz.netfliz.model.response.MovieByGenreResponse;
import com.netfliz.netfliz.repository.CustomMovieRepository;
import com.netfliz.netfliz.repository.MovieAssetRepository;
import com.netfliz.netfliz.repository.MovieImageRepository;
import com.netfliz.netfliz.repository.MovieRepository;
import com.netfliz.netfliz.validator.MovieAssetValidator;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService implements MoviesApiDelegate {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieValidator movieValidator;
    private final CustomMovieRepository customMovieRepository;
    private final RedisService redisService;
    private final MovieImageRepository movieImageRepository;
    private final MovieImageMapper movieImageMapper;
    private final MovieAssetRepository movieAssetRepository;
    private final MovieAssetValidator movieAssetValidator;
    private final MovieAssetMapper movieAssetMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MoviePage> getAllMovie(Integer page, Integer pageSize, String filter, String sort) {
        Specification<MovieEntity> specification = null;
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (filter != null && !filter.isEmpty()) {
            String[] filterArray = filter.split(" ");
            String field = Arrays.stream(filterArray).findFirst().get();
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
    public ResponseEntity<Movie> getMovieById(Long movieId) {
        movieValidator.validateMovieExist(movieId);

        MovieEntity movieEntity = movieRepository.findById(movieId).orElseThrow(
                () -> new NotFoundException("Movie not found with id: " + movieId)
        );

        Movie movie = movieMapper.mapFromEntity(movieEntity);

        // mapImage
        List<MovieImageEntity> movieImages = movieImageRepository.findByObjectIdAndObjectType(
                movieEntity.getId(), MovieObjectType.MOVIE);
        movie.setImages(movieImageMapper.mapFromEntities(movieImages));

        return ResponseEntity.ok(movie);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Movie> createMovie(Movie movie) {
        MovieEntity movieEntity = movieMapper.mapToEntity(movie);
        movieRepository.save(movieEntity);

        // save movie image
        List<MovieImage> movieImages = movie.getImages();
        if (!CollectionUtils.isEmpty(movieImages)) {
            movieValidator.validateMovieImage(movieImages);
            movieImageRepository.saveAll(movieImageMapper.mapToEntities(
                    movieImages, movieEntity.getId(), MovieObjectType.MOVIE));
        }

        // save movie assets
        List<MovieAsset> movieAssets = movie.getAssets();
        if (!CollectionUtils.isEmpty(movieAssets)) {
            movieAssetValidator.validateAssets(movieAssets);
            movieAssetRepository.saveAll(movieAssetMapper.mapToEntities(
                    movieEntity.getId(), MovieObjectType.MOVIE, movieAssets));
        }

        return ResponseEntity.ok(movie);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> updateMovie(Long movieId, Movie movie) {
        movieValidator.validateMovieExist(movieId);
        if (!movieRepository.existsById(movieId)) {
            throw new NotFoundException("Movie not found with id: " + movieId);
        }

        MovieEntity movieEntity = movieMapper.mapToEntity(movie);
        movieEntity.setId(movieId);
        movieRepository.save(movieEntity);

        // update movie image
        updateMovieImage(movieEntity.getId(), movie);

        // update movie assets
        updateMovieAssets(movieEntity.getId(), movie);

        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(Long movieId) {
        movieValidator.validateMovieExist(movieId);
        if (!movieRepository.existsById(movieId)) {
            throw new NotFoundException("Movie not found with id: " + movieId);
        }

        movieRepository.deleteById(movieId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Movie>> bulkMovie(List<Movie> movies) {
        List<MovieEntity> entityList = movies.stream().map(movieMapper::mapToEntity).toList();
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
        if (CollectionUtils.isEmpty(listDto)) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        Map<String, List<MovieByGenreDto>> map = listDto.stream().collect(Collectors.groupingBy(MovieByGenreDto::getName));
        List<MovieByGenreResponse> responses = new ArrayList<>();

        map.forEach((key, value) -> {
            MovieByGenreResponse response = new MovieByGenreResponse();
            response.setGenre(key);

            List<Movie> movies = movieMapper.mapMovieByGenreDtoToMovieList(value);

            // map to movies
            mapMovieImageAndAsset(movies, listDto.stream().map(MovieByGenreDto::getId).collect(Collectors.toSet()));

            response.setMovies(movies);
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
                case "in" -> " like " + "%" + value + "%";
                case "nin" -> " not like " + "%" + value + "%";
                default -> " = " + value;
            };
        }

        return query;
    }

    public void updateMovieImage(Long movieId, Movie movie) {
        if (CollectionUtils.isEmpty(movie.getImages())) {
            return;
        }

        // validate image
        movieValidator.validateMovieImage(movie.getImages());

        // update movie image
        List<MovieImage> movieImages = getUpdateImage(movie.getImages(), movieId);

        // find and delete old image which has type in movieImages
        List<Integer> imageTypes = movieImages.stream().map(MovieImage::getType).toList();
        List<Long> oldImageIds = movieImageRepository
                .findByObjectIdAndObjectTypeAndImageTypeIn(
                        movieId,
                        MovieObjectType.MOVIE,
                        imageTypes.stream().map(MovieImageType::fromId).toList())
                .stream()
                .map(MovieImageEntity::getId)
                .toList();

        if (!CollectionUtils.isEmpty(oldImageIds)) {
            movieImageRepository.deleteAllById(oldImageIds);
        }

        // save new image
        if (!CollectionUtils.isEmpty(movieImages)) {
            movieImageRepository.saveAll(movieImageMapper.mapToEntities(
                    movieImages, movieId, MovieObjectType.MOVIE));
        }
    }

    public void updateMovieAssets(Long movieId, Movie movie) {
        if (CollectionUtils.isEmpty(movie.getAssets())) {
            return;
        }
        movieAssetValidator.validateAssets(movie.getAssets());
        List<MovieAsset> updatedAssets = getUpdateAssets(movieId, movie.getAssets());

        // Xóa tất cả assets
        movieAssetRepository.deleteAllByObjectId(
                movieId,
                MovieObjectType.MOVIE,
                List.of(MovieAssetType.VIDEO, MovieAssetType.TRAILER, MovieAssetType.SUBTITLE));

        if (!CollectionUtils.isEmpty(updatedAssets)) {
            movieAssetRepository.saveAll(movieAssetMapper.mapToEntities(movieId, MovieObjectType.MOVIE, updatedAssets));
        }
    }

    /**
     * Lấy ra các image cần update (chưa tồn tại trong db)
     */
    public List<MovieImage> getUpdateImage(List<MovieImage> images, Long movieId) {
        Map<Long, List<MovieImageEntity>> map = movieImageRepository.findByObjectIdAndObjectType(movieId, MovieObjectType.MOVIE)
                .stream()
                .collect(Collectors.groupingBy(MovieImageEntity::getFileId));

        List<MovieImage> updated = new ArrayList<>();
        images.forEach(image -> {
            if (map.containsKey(image.getId())) {
                return;
            }
            updated.add(image);
        });

        return updated;
    }

    public List<MovieAsset> getUpdateAssets(Long movieId, List<MovieAsset> assets) {
        Map<Long, List<MovieAssetEntity>> map = movieAssetRepository.findByObjectId(movieId, MovieObjectType.MOVIE)
                .stream()
                .collect(Collectors.groupingBy(MovieAssetEntity::getFileId));

        List<MovieAsset> updated = new ArrayList<>();
        assets.forEach(asset -> {
            if (map.containsKey(asset.getFileId())) {
                return;
            }
            updated.add(asset);
        });

        return updated;
    }

    public MoviePage buildPage(Page<MovieEntity> resultPage) {
        MoviePage moviePage = new MoviePage();

        moviePage.setPage(resultPage.getNumber() + 1);
        moviePage.setPageSize(resultPage.getSize());
        moviePage.setTotalPages(resultPage.getTotalPages());
        moviePage.setTotal((int) resultPage.getTotalElements());

        List<Movie> movies = movieMapper.mapMovieEntityListToMovieList(resultPage.getContent());

        // map to movies
        mapMovieImageAndAsset(movies, movies.stream().map(Movie::getId).collect(Collectors.toSet()));
        
        moviePage.setItems(movies);

        return moviePage;
    }

    private void mapMovieImageAndAsset(List<Movie> movies, Collection<Long> movieIds) {
        Map<Long, List<MovieImageEntity>> mapImage = movieImageRepository
                .findByObjectIdsAndObjectType(movieIds, MovieObjectType.MOVIE)
                .stream()
                .collect(Collectors.groupingBy(MovieImageEntity::getObjectId));

        Map<Long, List<MovieAssetEntity>> mapAsset = movieAssetRepository.findByObjectIds(movieIds, MovieObjectType.MOVIE)
                .stream()
                .collect(Collectors.groupingBy(MovieAssetEntity::getObjectId));

        movies.forEach(movie -> {
            Optional.ofNullable(mapImage.get(movie.getId()))
                    .ifPresent(movieImages ->
                            movie.setImages(movieImages.stream().map(movieImageMapper::mapFromEntity).toList())
                    );
            Optional.ofNullable(mapAsset.get(movie.getId()))
                    .ifPresent(movieAssets ->
                            movie.setAssets(movieAssets.stream().map(movieAssetMapper::mapFromEntity).toList())
                    );
        });
    }
}
