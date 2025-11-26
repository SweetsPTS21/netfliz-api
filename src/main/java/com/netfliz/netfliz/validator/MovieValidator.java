package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import com.netfliz.netfliz.entity.enums.MovieType;
import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.model.MovieImage;
import com.netfliz.netfliz.repository.MovieRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MovieValidator {
    MovieRepository movieRepository;

    public MovieValidator(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void validateMovieExist(Long movieId) {
        if (movieRepository.existsById(movieId)) {
            return;
        }
        throw new NotFoundException("Movie does not exist");
    }

    public void validateMovieImage(List<MovieImage> images) {
        images.forEach(image -> {
            if (image.getId() == null) {
                throw new ValidationException("File ảnh không tồn tại cho loại " + MovieImageType.fromId(image.getType()));
            }
        });

        // các image type không phải gallery sẽ giới hạn 1
        Map<Integer, List<MovieImage>> map = images.stream().collect(Collectors.groupingBy(MovieImage::getType));
        map.forEach((type, value) -> {
            if (!Objects.equals(type, MovieImageType.GALLERY.getId()) && value.size() > 1) {
                throw new ValidationException("Phim chỉ có thể có một " + MovieImageType.fromId(type));
            }
        });
    }

    public void validateSeriesMovie(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId).orElseThrow(
                () -> new NotFoundException("Phim không tồn tại")
        );

        if (!MovieType.SERIES.getValue().equalsIgnoreCase(movie.getType())) {
            throw new ValidationException("Phim không phải là phim bộ");
        }
    }
}
