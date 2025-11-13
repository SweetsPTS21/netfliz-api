package com.netfliz.netfliz.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class MovieByGenreRequest {
    private List<String> genres;
    private Integer limit;

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

        if (limit == null) {
            limit = 10;
        }
    }
}
