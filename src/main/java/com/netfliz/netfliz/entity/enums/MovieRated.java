package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieRated {
    G("G", "Dành cho mọi lứa tuổi"),
    PG("PG", "Hướng dẫn của phụ huynh"),
    PG_13("PG-13", "Cấm trẻ dưới 13 tuổi nếu không có người lớn"),
    C13("C13", "Cấm khán giả dưới 13 tuổi"),
    TV_14("TV-14", "Không phù hợp cho trẻ dưới 14 tuổi"),
    R("R", "Hạn chế – cần người lớn đi kèm"),
    C15("C15", "Cấm khán giả dưới 15 tuổi"),
    C16("C16", "Cấm khán giả dưới 16 tuổi"),
    NC_17("NC-17", "Cấm khán giả dưới 17 tuổi"),
    NOT_RATED("NOT_RATED", "Chưa được xếp hạng");

    private final String value;
    private final String description;

    public static MovieRated fromValue(String value) {
        for (MovieRated m : values()) {
            if (m.value.equalsIgnoreCase(value)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid movie rated value: " + value);
    }
}

