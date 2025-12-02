package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.model.MovieEpisode;
import com.netfliz.netfliz.repository.MovieEpisodeRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieEpisodeValidator {
    private final MovieEpisodeRepository movieEpisodeRepository;
    private final MovieAssetValidator movieAssetValidator;

    public void validateEpisode(Long movieId, MovieEpisode episode) {
        if (episode.getEpisodeNumber() == null) {
            throw new ValidationException("Số tập phim không được để trống");
        }

        if (episode.getEpisodeNumber() <= 0) {
            throw new ValidationException("Số tập phim phải lớn hơn 0");
        }

        if (movieEpisodeRepository.existsByMovieIdAndEpisodeNumber(movieId, episode.getEpisodeNumber())) {
            throw new ValidationException("Số tập phim đã tồn tại");
        }

        if (movieEpisodeRepository.existsByMovieIdAndEpisodeOrder(movieId, episode.getEpisodeOrder())) {
            throw new ValidationException("Thứ tự tập phim đã tồn tại");
        }

        if (episode.getEpisodeOrder() <= 0) {
            throw new ValidationException("Thứ tự tập phim phải lớn hơn 0");
        }

        if (episode.getName() == null) {
            throw new ValidationException("Tên tập phim không được để trống");
        }

        movieAssetValidator.validateAssets(episode.getAssets());
    }
}
