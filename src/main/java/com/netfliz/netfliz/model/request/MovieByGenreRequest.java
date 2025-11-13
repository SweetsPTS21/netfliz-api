package com.netfliz.netfliz.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovieByGenreRequest extends BaseRequest {
    private List<String> genres;

    public void validate() {
        if (CollectionUtils.isEmpty(genres)) {
            throw new IllegalArgumentException("Genres is required");
        }

        normalize();
    }

    public void normalize() {
        if (CollectionUtils.isEmpty(genres)) {
            genres = new ArrayList<>();
        }

        super.normalize();
    }
}
