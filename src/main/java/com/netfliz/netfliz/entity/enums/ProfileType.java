package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileType {
    DEFAULT(0, "DEFAULT"),
    KIDS(1, "KIDS"),
    GUEST(2, "GUEST");

    private final Integer id;
    private final String name;

    public static ProfileType fromId(Integer id){
        for (ProfileType profileType : ProfileType.values()) {
            if (profileType.getId().equals(id)) {
                return profileType;
            }
        }
        return null;
    }
}
