package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.request.MovieByGenreRequest;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import com.netfliz.netfliz.model.response.MovieByCategoryResponse;
import com.netfliz.netfliz.model.MoviePage;
import com.netfliz.netfliz.model.response.MovieByGenreResponse;
import com.netfliz.netfliz.service.MovieService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MovieGraphql {
    private final MovieService movieService;

    public MovieGraphql(MovieService movieService) {
        this.movieService = movieService;
    }

    @QueryMapping
    public MoviePage getAllMovie(@Argument int page, @Argument int pageSize, @Argument String filter, @Argument String sort) {
        ResponseEntity<MoviePage> responseEntity = movieService.getAllMovie(page, pageSize, filter, sort);
        return responseEntity.getBody();
    }

    @QueryMapping
    public List<MovieByCategoryResponse> getMovieByCategory() {
        ResponseEntity<MoviePage> responseEntity = movieService.getAllMovie(1, 20, null, null);
        List<Movie> movies = Optional.ofNullable(responseEntity.getBody())
                .map(MoviePage::getItems)
                .orElse(new ArrayList<>());

        if (CollectionUtils.isEmpty(movies)) {
            return new ArrayList<>();
        }

        List<MovieByCategoryResponse> movieByCategories = movies.stream()
                .flatMap(movie -> movie.getGenre().stream()) // Directly use genre stream
                .distinct() // Remove duplicate genres (optional)
                .map(genre -> {
                    List<Movie> movieList = movies.stream()
                            .filter(movie -> movie.getGenre().contains(genre))
                            .collect(Collectors.toList());
                    return MovieByCategoryResponse.builder()
                            .category(genre)
                            .movies(movieList)
                            .build();
                }).toList();


        return movieByCategories;
    }

    @QueryMapping
    public List<MovieByGenreResponse> getMoviesByGenres(@Argument MovieByGenreRequest request) {
        ResponseEntity<List<MovieByGenreResponse>> responseEntity = movieService.getMoviesByGenres(request);
        return responseEntity.getBody();
    }

    @QueryMapping
    public MoviePage getMoviesByFilter(@Argument MovieFilterRequest request) {
        ResponseEntity<MoviePage> responseEntity = movieService.getMoviesByFilter(request);
        return responseEntity.getBody();
    }

    @QueryMapping
    public Movie getMovieById(@Argument Long id) {
        ResponseEntity<Movie> responseEntity = movieService.getMovieById(id);
        return responseEntity.getBody();
    }

    @MutationMapping
    public Movie createMovie(@Argument Movie movie) {
        ResponseEntity<Movie> responseEntity = movieService.createMovie(movie);
        return responseEntity.getBody();
    }

    @MutationMapping
    public List<Movie> createListMovie(@Argument List<Movie> movies) {
        ResponseEntity<List<Movie>> responseEntity = movieService.bulkMovie(movies);
        return responseEntity.getBody();
    }

    @MutationMapping
    public boolean updateMovie(@Argument Long id,@Argument Movie movie) {
        ResponseEntity<Void> responseEntity = movieService.updateMovie(id, movie);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @MutationMapping
    public void deleteMovie(@Argument Long id) {
        ResponseEntity<Void> responseEntity = movieService.deleteMovie(id);
    }
}
