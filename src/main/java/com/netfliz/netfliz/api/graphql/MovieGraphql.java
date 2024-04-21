package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.MovieByCategory;
import com.netfliz.netfliz.service.MovieService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
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
    public List<MovieByCategory> getMovieByCategory() {
        ResponseEntity<List<Movie>> responseEntity = movieService.getAllMovie();
        List<Movie> movies = responseEntity.getBody();

        if (movies == null) {
            return null;
        }

        List<MovieByCategory> movieByCategories = movies.stream()
                .flatMap(movie -> Arrays.stream(movie.getGenre().split(", "))) // Split genres and create new stream
                .distinct() // Remove duplicate genres (optional)
                .map(genre -> MovieByCategory.builder() // Create MovieByCategory per genre
                        .category(genre.trim()) // Trim leading/trailing whitespace (optional)
                        .movies(movies.stream() // Filter movies for this genre
                                .filter(m -> m.getGenre().contains(genre)) // Check if genre exists in movie genres
                                .collect(Collectors.toList())) // Collect filtered movies into a list
                        .build())
                .collect(Collectors.toList());


        return movieByCategories;
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
