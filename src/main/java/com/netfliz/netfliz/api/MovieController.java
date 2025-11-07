package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.MoviePage;
import com.netfliz.netfliz.service.MovieService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController implements MoviesApi {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public ResponseEntity<MoviePage> getAllMovie(Integer page, Integer pageSize, String filter, String sort) {
        return movieService.getAllMovie(page, pageSize, filter, sort);
    }

    @Override
    public ResponseEntity<Movie> getMovieById(@Argument Long movieId) {
        return movieService.getMovieById(movieId);
    }

    @Override
    public ResponseEntity<Movie> createMovie(@Argument Movie movie) {
        return movieService.createMovie(movie);
    }

    @Override
    public ResponseEntity<Void> updateMovie(@Argument Long movieId,@Argument Movie movie) {
        return movieService.updateMovie(movieId, movie);
    }

    @Override
    public ResponseEntity<Void> deleteMovie(@Argument Long movieId) {
        return movieService.deleteMovie(movieId);
    }
}
