package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.service.MovieService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieGraphql {
    private final MovieService movieService;

    public MovieGraphql(MovieService movieService) {
        this.movieService = movieService;
    }

    @QueryMapping
    public List<Movie> getAllMovie() {
        ResponseEntity<List<Movie>> responseEntity = movieService.getAllMovie();
        return responseEntity.getBody();
    }

    @QueryMapping
    public Movie getMovieById(@Argument String movieId) {
        ResponseEntity<Movie> responseEntity = movieService.getMovieById(movieId);
        return responseEntity.getBody();
    }

    @MutationMapping
    public Movie createMovie(@Argument Movie movie) {
        ResponseEntity<Movie> responseEntity = movieService.createMovie(movie);
        return responseEntity.getBody();
    }

    @MutationMapping
    public boolean updateMovie(@Argument String movieId,@Argument Movie movie) {
        ResponseEntity<Void> responseEntity = movieService.updateMovie(movieId, movie);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @MutationMapping
    public void deleteMovie(@Argument String movieId) {
        ResponseEntity<Void> responseEntity = movieService.deleteMovie(movieId);
    }
}
