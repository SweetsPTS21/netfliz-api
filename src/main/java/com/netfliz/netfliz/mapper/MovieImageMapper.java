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

    public List<MovieImageEntity> mapToEntities(List<MovieImage> from, Long movieId) {
        return from.stream().map(movieImage -> {
            MovieImageEntity imageEntity = new MovieImageEntity();
            imageEntity.setMovieId(movieId);
            imageEntity.setFileId(movieImage.getId());
            imageEntity.setImageType(MovieImageType.fromId(movieImage.getType()));
            imageEntity.setImageUrl(movieImage.getUrl());

            return imageEntity;
        }).toList();
    }
}
