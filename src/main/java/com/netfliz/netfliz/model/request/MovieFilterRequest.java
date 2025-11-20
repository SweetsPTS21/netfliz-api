package com.netfliz.netfliz.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovieFilterRequest extends BaseRequest {
    private List<String> genres;
    private List<String> countries;
    private List<String> languages;
    private String year;
    private String rating;
    private String rated;
    private String type;
    private String sort;
    private String title;

    public void validate() {
        normalize();
    }

    public void normalize() {
        if (CollectionUtils.isEmpty(genres)) {
            genres = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(countries)) {
            countries = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(languages)) {
            languages = new ArrayList<>();
        }

        super.normalize();
    }
}
