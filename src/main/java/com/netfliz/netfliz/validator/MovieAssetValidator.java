package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.model.MovieAsset;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class MovieAssetValidator {
    public void validateAssets(List<MovieAsset> assets) {
        if (!CollectionUtils.isEmpty(assets)) {
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
}
