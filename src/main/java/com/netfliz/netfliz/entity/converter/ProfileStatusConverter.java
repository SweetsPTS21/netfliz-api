package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.ProfileStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class ProfileStatusConverter implements AttributeConverter<ProfileStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProfileStatus ProfileStatus) {
        return Objects.isNull(ProfileStatus) ? null : ProfileStatus.getId();
    }

    @Override
    public ProfileStatus convertToEntityAttribute(Integer integer) {
        return Objects.isNull(integer) ? null : ProfileStatus.fromId(integer);
    }
}
