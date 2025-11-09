package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.ProfileType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class ProfileTypeConverter implements AttributeConverter<ProfileType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProfileType profileType) {
        return Objects.isNull(profileType) ? null : profileType.getId();
    }

    @Override
    public ProfileType convertToEntityAttribute(Integer integer) {
        return Objects.isNull(integer) ? null : ProfileType.fromId(integer);
    }
}
