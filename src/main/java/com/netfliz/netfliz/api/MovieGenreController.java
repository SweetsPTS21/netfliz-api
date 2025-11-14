package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.MovieGenre;
import com.netfliz.netfliz.service.MovieGenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MovieGenreController implements MovieGenresApi {
    private final MovieGenreService movieGenreService;

    @Override
    public ResponseEntity<List<MovieGenre>> getAllMovieGenres() {
        return movieGenreService.getAllMovieGenres();
    }

    @Override
    public ResponseEntity<MovieGenre> getMovieGenreById(Integer id) {
        return movieGenreService.getMovieGenreById(id);
    }

    @Override
    public ResponseEntity<MovieGenre> createMovieGenre(MovieGenre movieGenre) {
        return movieGenreService.createMovieGenre(movieGenre);
    }

    @Override
    public ResponseEntity<MovieGenre> updateMovieGenreById(Integer id, MovieGenre movieGenre) {
        return movieGenreService.updateMovieGenreById(id, movieGenre);
    }

    @Override
    public ResponseEntity<Boolean> deleteMovieGenreById(Integer id) {
        return movieGenreService.deleteMovieGenreById(id);
    }
}
