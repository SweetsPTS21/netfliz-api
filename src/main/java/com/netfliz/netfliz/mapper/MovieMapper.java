package com.netfliz.netfliz.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.model.MovieByGenreDto;
import com.netfliz.netfliz.service.FileService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Component
public class MovieMapper {

    private final Logger logger = Logger.getLogger(MovieMapper.class.getName());
    private final FileService fileService;

    public MovieMapper(FileService fileService) {
        this.fileService = fileService;
    }


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
        to.setPosterId(ObjectUtils.isEmpty(from.getNfFileId()) ? 0 : from.getNfFileId());
        to.setMetaScore(ObjectUtils.isEmpty(from.getMetaScore()) ? 0 : from.getMetaScore());
        to.setImdbRating(from.getImdbRating());
        to.setImdbVotes(ObjectUtils.isEmpty(from.getImdbVotes()) ? 0 : from.getImdbVotes());
        to.setType(from.getType());
        to.setResponse(from.getResponse());
        to.setImages(mapListToString(from.getImages()));

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
        to.setPoster(getPosterLinkById(from.getPosterId()));
        to.setNfFileId(from.getPosterId());
        to.setMetaScore(from.getMetaScore());
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

    public List<Movie> mapMovieByGenreDtoToMovieList(List<MovieByGenreDto> from) {
        return from.stream().map(this::mapMovieEntityToMovie).toList();
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

    private String getPosterLinkById(long posterId) {
        AtomicReference<String> poster = new AtomicReference<>("");

        Optional.ofNullable(fileService.getFileById(posterId)).ifPresent(file -> {
            poster.set(file.getFileDownloadUri());
        });

        return poster.get();
    }
}
