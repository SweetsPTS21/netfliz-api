package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.enums.MovieAssetType;
import com.netfliz.netfliz.entity.enums.MovieObjectType;
import com.netfliz.netfliz.model.MovieAsset;
import com.netfliz.netfliz.util.JsonUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MovieAssetMapper {
    public MovieAsset mapFromEntity(MovieAssetEntity from) {
        MovieAsset to = new MovieAsset();

        to.setId(from.getId());
        to.setFileId(from.getFileId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());
        to.assetType(from.getAssetType().getId());
        to.setFormat(from.getFormat());
        to.setDrm(JsonUtils.serialize(from.getDrm()));
        to.setRenditions(JsonUtils.serialize(from.getRendition()));

        return to;
    }

    public MovieAssetEntity mapToEntity(Long objectId, MovieObjectType objectType, MovieAsset from) {
        MovieAssetEntity to = new MovieAssetEntity();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setObjectId(objectId);
        to.setObjectType(objectType);
        to.setUrl(from.getUrl());
        to.setAssetType(MovieAssetType.fromId(from.getAssetType()));
        to.setFormat(from.getFormat());
        to.setFileId(Objects.isNull(from.getFileId()) ? 0L : from.getFileId());

        if (Strings.isNotBlank(from.getDrm())) {
            to.setDrm(JsonUtils.parse(from.getDrm()));
        }

        if (Strings.isNotBlank(from.getRenditions())) {
            to.setRendition(JsonUtils.parse(from.getRenditions()));
        }

        return to;
    }

    public List<MovieAssetEntity> mapToEntities(Long objectId, MovieObjectType objectType, List<MovieAsset> from) {
        return from.stream().map(movieAsset -> mapToEntity(objectId, objectType, movieAsset)).toList();
    }

    public List<MovieAsset> mapFromEntities(List<MovieAssetEntity> from) {
        return from.stream().map(this::mapFromEntity).toList();
    }
}
