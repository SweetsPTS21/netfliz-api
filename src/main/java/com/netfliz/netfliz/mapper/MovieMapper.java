package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.Movie;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MovieMapper {

    public MovieEntity mapMovieToMovieEntity(Movie from) {
        MovieEntity to = new MovieEntity();

        to.setId(from.getId().toString());
        to.setTitle(from.getTitle());
        to.setYear(Integer.parseInt(from.getYear().toString()));
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(from.getGenre());
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguage(from.getLanguage());
        to.setCountry(from.getCountry());
        to.setAwards(from.getAwards());
        to.setPoster(from.getPoster());
        to.setMetascore(from.getMetaScore());
        to.setImdbRating(from.getImdbRating());
        to.setImdbVotes(from.getImdbVotes());
        to.setType(from.getType());
        to.setResponse(from.getResponse());
        to.setImages(Collections.singletonList(from.getImages()));

        return to;
    }

    public Movie mapMovieEntityToMovie(MovieEntity from) {
        Movie to = new Movie();

        to.setId(Long.parseLong(from.getId()));
        to.setTitle(from.getTitle());
        to.setYear((long) from.getYear());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(from.getGenre());
        to.setDirector(from.getDirector());
        to.setWriter(from.getWriter());
        to.setActors(from.getActors());
        to.setPlot(from.getPlot());
        to.setLanguage(from.getLanguage());
        to.setCountry(from.getCountry());
        to.setAwards(from.getAwards());
        to.setPoster(from.getPoster());
        to.setMetaScore(from.getMetascore());
        to.setImdbRating(from.getImdbRating());
        to.setImdbVotes(from.getImdbVotes());
        to.setType(from.getType());
        to.setResponse(from.getResponse());
        to.setImages(from.getImages().get(0));

        return to;
    }

    public List<Movie> mapMovieEntityListToMovieList(List<MovieEntity> from) {
        return from.stream().map(this::mapMovieEntityToMovie).toList();
    }

    public List<MovieEntity> mapMovieListToMovieEntityList(List<Movie> from) {
        return from.stream().map(this::mapMovieToMovieEntity).toList();
    }
}
