package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieImageType {
    POSTER(1, "Poster"),
    LANDSCAPE(2, "Landscape"),
    BACKDROP(3, "Backdrop"),
    THUMBNAIL(4, "thumbnail"),
    LOGO(5, "Logo"),
    GALLERY(6, "gallery");

    private final Integer id;
    private final String name;

    public static MovieImageType fromId(Integer id) {
        for (MovieImageType type : MovieImageType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }
}
