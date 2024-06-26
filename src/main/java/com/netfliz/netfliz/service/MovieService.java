package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MoviesApiDelegate;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.mapper.MovieMapper;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.repository.IMovieRepository;
import com.netfliz.netfliz.validator.MovieValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<List<Movie>> getAllMovie() {
        List<Movie> movies = movieMapper.mapMovieEntityListToMovieList(movieRepository.findAll());
        return ResponseEntity.ok(movies);
    }

    @Override
    public ResponseEntity<Movie> getMovieById(String movieId) {
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
    public ResponseEntity<Void> updateMovie(String movieId, Movie movie) {
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
    public ResponseEntity<Void> deleteMovie(String movieId) {
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
}
