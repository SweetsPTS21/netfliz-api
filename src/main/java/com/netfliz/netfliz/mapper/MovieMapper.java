package com.netfliz.netfliz.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.MovieByGenreDto;
import com.netfliz.netfliz.util.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class MovieMapper {
    public MovieEntity mapToEntity(Movie from) {
        MovieEntity to = new MovieEntity();

        to.setTitle(from.getTitle());
        to.setYear(from.getYear().intValue());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(JsonUtils.parse(from.getGenre()));
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguages(JsonUtils.parse(from.getLanguages()));
        to.setCountries(JsonUtils.parse(from.getCountries()));
        to.setAwards(from.getAwards());
        to.setMetaScore(ObjectUtils.isEmpty(from.getMetaScore()) ? 0 : from.getMetaScore());
        to.setImdbRating(from.getImdbRating());
        to.setType(from.getType());
        to.setCategories(JsonUtils.parse(from.getCategories()));

        // update date
        to.setUpdatedAt(new Date());

        return to;
    }

    public Movie mapFromEntity(MovieEntity from) {
        Movie to = new Movie();

        to.setId(from.getId());
        to.setTitle(from.getTitle());
        to.setYear((long) from.getYear());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(parseList(from.getGenre()));
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguages(parseList(from.getLanguages()));
        to.setCountries(parseList(from.getCountries()));
        to.setAwards(from.getAwards());
        to.setMetaScore(from.getMetaScore());
        to.setImdbRating(from.getImdbRating());
        to.setType(from.getType());
        to.setCategories(parseList(from.getCategories()));

        return to;
    }

    public List<Movie> mapMovieEntityListToMovieList(List<MovieEntity> from) {
        return from.stream().map(this::mapFromEntity).toList();
    }

    public List<Movie> mapMovieByGenreDtoToMovieList(List<MovieByGenreDto> from) {
        return from.stream().map(this::mapFromEntity).toList();
    }

    private static List<String> parseList(JsonNode jsonNode) {
        if (Objects.isNull(jsonNode)) {
            return Collections.emptyList();
        }

        return JsonUtils.parseList(jsonNode.toString(), String.class);
    }
}
