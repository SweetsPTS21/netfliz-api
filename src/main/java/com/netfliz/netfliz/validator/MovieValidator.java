package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.repository.IMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovieValidator {
    IMovieRepository movieRepository;

    public MovieValidator(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void validateMovieExist(Long movieId) {
        if (movieRepository.existsById(movieId)) {
            return;
        }
        throw new NotFoundException("Movie does not exist");
    }
}
