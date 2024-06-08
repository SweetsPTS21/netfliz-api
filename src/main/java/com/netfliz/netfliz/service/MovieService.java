package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MoviesApiDelegate;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.mapper.MovieMapper;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.repository.IMovieRepository;
import com.netfliz.netfliz.validator.MovieValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService implements MoviesApiDelegate {
    IMovieRepository movieRepository;

    private final MovieMapper movieMapper;
    private final MovieValidator movieValidator;

    public MovieService(IMovieRepository movieRepository,MovieMapper movieMapper, MovieValidator movieValidator) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.movieValidator = movieValidator;
    }

    @Override
    public ResponseEntity<List<Movie>> getAllMovie(Integer page, Integer pageSize, String filter, String sort) {
        Specification<MovieEntity> specification = null;

        Pageable pageable = PageRequest.of(page != null ? page : 0, pageSize != null ? pageSize : 10, Sort.by(Sort.Direction.DESC, "imdbRating"));

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
            List<Movie> result = movieMapper.mapMovieEntityListToMovieList(resultPage.getContent());

            return ResponseEntity.ok(result);
        }

        List<MovieEntity> resultPage = movieRepository.findAll();
        List<Movie> result = movieMapper.mapMovieEntityListToMovieList(resultPage);

        return ResponseEntity.ok(result);
    }

    @Override
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
    public ResponseEntity<Movie> createMovie(Movie movie) {
        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieRepository.save(movieEntity);
        return ResponseEntity.ok(movie);
    }

    @Override
    public ResponseEntity<Void> updateMovie(Long movieId, Movie movie) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieEntity.setId(Math.toIntExact(movieId));
        movieRepository.save(movieEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteMovie(Long movieId) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        movieRepository.deleteById(movieId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<Movie>> bulkMovie(List<Movie> movies) {
        movies.forEach(movie -> {
            MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
            movieRepository.save(movieEntity);
        });

        return ResponseEntity.ok(movies);
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
}
