package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import com.netfliz.netfliz.model.MovieImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieImageMapper {
    public MovieImage mapFromEntity(MovieImageEntity from) {
        MovieImage to = new MovieImage();
        to.setId(from.getId());
        to.setType(from.getImageType().getId());
        to.setUrl(from.getImageUrl());
        return to;
    }

    public MovieImageEntity mapToEntity(MovieImage from, Long movieId) {
        MovieImageEntity to = new MovieImageEntity();
        to.setMovieId(movieId);
        to.setFileId(from.getId());
        to.setImageType(MovieImageType.fromId(from.getType()));
        to.setImageUrl(from.getUrl());
        return to;
    }

    public List<MovieImage> mapFromEntities(List<MovieImageEntity> from) {
        return from.stream().map(this::mapFromEntity).toList();
    }

    public List<MovieImageEntity> mapToEntities(List<MovieImage> from, Long movieId) {
        return from.stream().map(movieImage -> mapToEntity(movieImage, movieId)).toList();
    }
}
