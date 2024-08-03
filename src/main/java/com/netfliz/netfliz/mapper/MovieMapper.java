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

        to.setTitle(from.getTitle());
        to.setYear(from.getYear().intValue());
        to.setTrailer(from.getTrailer());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(from.getGenre());
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
        to.setImages(from.getImages());

        return to;
    }

    public Movie mapMovieEntityToMovie(MovieEntity from) {
        Movie to = new Movie();

        to.setId(from.getId());
        to.setTitle(from.getTitle());
        to.setYear((long) from.getYear());
        to.setTrailer(from.getTrailer());
        to.setRated(from.getRated());
        to.setReleased(from.getReleased());
        to.setRuntime(from.getRuntime());
        to.setGenre(from.getGenre());
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
        to.setImages(from.getImages());

        return to;
    }

    public List<Movie> mapMovieEntityListToMovieList(List<MovieEntity> from) {
        return from.stream().map(this::mapMovieEntityToMovie).toList();
    }

    public List<MovieEntity> mapMovieListToMovieEntityList(List<Movie> from) {
        return from.stream().map(this::mapMovieToMovieEntity).toList();
    }
}
