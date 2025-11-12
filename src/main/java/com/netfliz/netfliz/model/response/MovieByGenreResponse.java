package com.netfliz.netfliz.model.response;

import com.netfliz.netfliz.model.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieByGenreResponse {
    private String genre;
    private List<Movie> movies;
}
