package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import com.netfliz.netfliz.entity.enums.MovieObjectType;
import com.netfliz.netfliz.model.MovieImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MovieImageMapper {
    public MovieImage mapFromEntity(MovieImageEntity from) {
        MovieImage to = new MovieImage();

        to.setId(from.getId());
        to.setFileId(from.getFileId());
        to.setName(from.getName());
        to.setFormat(from.getFormat());
        to.setType(from.getImageType().getId());
        to.setUrl(from.getImageUrl());

        return to;
    }

    public MovieImageEntity mapToEntity(MovieImage from, Long objectId, MovieObjectType objectType) {
        MovieImageEntity to = new MovieImageEntity();

        to.setName(from.getName());
        to.setFormat(from.getFormat());
        to.setObjectId(objectId);
        to.setObjectType(objectType);

        to.setFileId(Objects.isNull(from.getId()) ? 0L : from.getId());
        to.setImageType(MovieImageType.fromId(from.getType()));
        to.setImageUrl(from.getUrl());

        return to;
    }

    public List<MovieImage> mapFromEntities(List<MovieImageEntity> from) {
        return from.stream().map(this::mapFromEntity).toList();
    }

    public List<MovieImageEntity> mapToEntities(List<MovieImage> from, Long objectId, MovieObjectType objectType) {
        return from.stream().map(movieImage -> mapToEntity(movieImage, objectId, objectType)).toList();
    }
}
