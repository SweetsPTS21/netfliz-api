package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.MovieImageType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class MovieImageTypeConverter implements AttributeConverter<MovieImageType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MovieImageType movieImageType) {
        return Objects.isNull(movieImageType) ? null : movieImageType.getId();
    }

    @Override
    public MovieImageType convertToEntityAttribute(Integer integer) {
        return Objects.isNull(integer) ? null : MovieImageType.fromId(integer);
    }
}
