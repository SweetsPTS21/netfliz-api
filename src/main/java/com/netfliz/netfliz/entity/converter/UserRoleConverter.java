package com.netfliz.netfliz.entity.converter;

import com.netfliz.netfliz.entity.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        return Objects.isNull(role) ? null : role.getName();
    }

    @Override
    public Role convertToEntityAttribute(String name) {
        return Strings.isBlank(name) ? null : Role.valueOf(name);
    }
}
