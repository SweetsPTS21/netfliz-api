package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public class MovieController implements MovieApi {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public ResponseEntity<List<Movie>> getAllMovie() {
        return movieService.getAllMovie();
    }

    @Override
    public ResponseEntity<Movie> getMovieById(String movieId) {
        return movieService.getMovieById(movieId);
    }

    @Override
    public ResponseEntity<Movie> createMovie(Movie movie) {
        return movieService.createMovie(movie);
    }

    @Override
    public ResponseEntity<Void> updateMovie(String movieId, Movie movie) {
        return movieService.updateMovie(movieId, movie);
    }

    @Override
    public ResponseEntity<Void> deleteMovie(String movieId) {
        return movieService.deleteMovie(movieId);
    }
}
