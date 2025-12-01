package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.model.MovieAsset;
import com.netfliz.netfliz.model.MovieEpisode;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class MovieEpisodeValidator {

    public void validateEpisode(MovieEpisode episode) {
        if (episode.getEpisodeNumber() == null) {
            throw new ValidationException("Số tập phim không được để trống");
        }

        if (episode.getEpisodeNumber() <= 0) {
            throw new ValidationException("Số tập phim phải lớn hơn 0");
        }

        if (episode.getEpisodeOrder() <= 0) {
            throw new ValidationException("Thứ tự tập phim phải lớn hơn 0");
        }

        if (episode.getName() == null) {
            throw new ValidationException("Tên tập phim không được để trống");
        }

        validateAssets(episode.getAssets());
    }

    private void validateAssets(List<MovieAsset> assets) {
        if (CollectionUtils.isEmpty(assets)) {
            throw new ValidationException("Assets không được để trống");
        }

        assets.forEach(asset -> {
            if (Strings.isBlank(asset.getUrl())) {
                throw new ValidationException("URL không được để trống");
            }

            if (asset.getAssetType() == null) {
                throw new ValidationException("Loại asset không được để trống");
            }
        });
    }
}
