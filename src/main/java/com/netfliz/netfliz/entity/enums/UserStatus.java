package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE(1, "ACTIVE"),
    INACTIVE(0, "INACTIVE"),
    BANNED(2, "BANNED");

    private final Integer id;
    private final String name;

    public static UserStatus fromId(Integer id) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }
}
