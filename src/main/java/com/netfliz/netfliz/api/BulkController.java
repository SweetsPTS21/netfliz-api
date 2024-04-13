package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.service.MovieService;
import com.netfliz.netfliz.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BulkController {
    private final MovieService movieService;

    public BulkController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/movie/bulk")
    public ResponseEntity<List<Movie>> bulkMovie(@RequestBody List<Movie> movies) {
        return movieService.bulkMovie(movies);
    }

}
