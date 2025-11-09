package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileStatus {
    ACTIVE(1, "ACTIVE"),
    INACTIVE(0, "INACTIVE"),
    LOCKED(2, "LOCKED");

    private final Integer id;
    private final String name;


    public static ProfileStatus fromId(Integer id){
        for (ProfileStatus profileStatus : ProfileStatus.values()) {
            if (profileStatus.getId().equals(id)) {
                return profileStatus;
            }
        }
        return null;
    }
}
