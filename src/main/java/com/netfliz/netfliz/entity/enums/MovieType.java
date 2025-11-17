package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieType {
    MOVIE("movie", "Phim lẻ"),
    SERIES("series", "Phim bộ"),
    TV("tv", "Chương trình truyền hình");

    private final String value;
    private final String description;

    public static MovieType fromValue(String value) {
        for (MovieType movieType : values()) {
            if (movieType.value.equalsIgnoreCase(value)) {
                return movieType;
            }
        }
        throw new IllegalArgumentException("Invalid movie type value: " + value);
    }
}
