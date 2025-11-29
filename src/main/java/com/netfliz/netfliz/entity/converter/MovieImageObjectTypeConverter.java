package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.MovieImageObjectType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class MovieImageObjectTypeConverter implements AttributeConverter<MovieImageObjectType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MovieImageObjectType movieImageObjectType) {
        return Objects.isNull(movieImageObjectType) ? null : movieImageObjectType.getId();
    }

    @Override
    public MovieImageObjectType convertToEntityAttribute(Integer integer) {
        return Objects.isNull(integer) ? null : MovieImageObjectType.fromId(integer);
    }
}
