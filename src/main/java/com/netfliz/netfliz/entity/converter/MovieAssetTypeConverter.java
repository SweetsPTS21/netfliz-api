package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.MovieAssetType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class MovieAssetTypeConverter implements AttributeConverter<MovieAssetType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MovieAssetType movieAssetType) {
        return Objects.isNull(movieAssetType) ? null : movieAssetType.getId();
    }

    @Override
    public MovieAssetType convertToEntityAttribute(Integer id) {
        return Objects.isNull(id) ? null : MovieAssetType.fromId(id);
    }
}
