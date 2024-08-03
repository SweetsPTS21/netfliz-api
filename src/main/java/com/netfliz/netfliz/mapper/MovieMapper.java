package com.netfliz.netfliz.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.Movie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Component
public class MovieMapper {

    private final Logger logger = Logger.getLogger(MovieMapper.class.getName());

    public MovieEntity mapMovieToMovieEntity(Movie from) {
        MovieEntity to = new MovieEntity();

        to.setTitle(from.getTitle());
        to.setYear(from.getYear().intValue());
        to.setTrailer(from.getTrailer());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(mapListToString(from.getGenre()));
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguages(from.getLanguages());
        to.setCountry(from.getCountry());
        to.setAwards(from.getAwards());
        to.setPoster(from.getPoster());
        to.setMetascore(from.getMetaScore());
        to.setImdbRating(from.getImdbRating());
        to.setImdbVotes(from.getImdbVotes());
        to.setType(from.getType());
        to.setResponse(from.getResponse());
        to.setImages(mapListToString(from.getImages()));

        to.setCreatedDate(new Date());
        to.setUpdatedDate(new Date());

        return to;
    }

    public Movie mapMovieEntityToMovie(MovieEntity from) {
        Movie to = new Movie();

        to.setId(Long.valueOf(from.getId()));
        to.setTitle(from.getTitle());
        to.setYear((long) from.getYear());
        to.setTrailer(from.getTrailer());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(mapStringToList(from.getGenre()));
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguages(from.getLanguages());
        to.setCountry(from.getCountry());
        to.setAwards(from.getAwards());
        to.setPoster(from.getPoster());
        to.setMetaScore(from.getMetascore());
        to.setImdbRating(from.getImdbRating());
        to.setImdbVotes(from.getImdbVotes());
        to.setType(from.getType());
        to.setResponse(from.isResponse());
        to.setImages(mapStringToList(from.getImages()));

        return to;
    }

    public List<Movie> mapMovieEntityListToMovieList(List<MovieEntity> from) {
        return from.stream().map(this::mapMovieEntityToMovie).toList();
    }

    public List<MovieEntity> mapMovieListToMovieEntityList(List<Movie> from) {
        return from.stream().map(this::mapMovieToMovieEntity).toList();
    }

    public List<String> mapStringToList(String genre) {
        if (genre == null) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(genre, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            logger.warning("Error parsing genre: " + genre);
        }

        return Collections.emptyList();
    }

    public String mapListToString(List<String> genre) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(genre);
        } catch (JsonProcessingException e) {
            logger.warning("Error parsing genre: " + genre);
        }

        return "";
    }
}
