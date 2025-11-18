package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieLanguage {
    VIETNAMESE("Vietnamese", "Tiếng Việt"),
    ENGLISH("English", "Tiếng Anh"),
    KOREAN("Korean", "Tiếng Hàn"),
    GERMAN("German", "Tiếng Đức"),
    FRENCH("French", "Tiếng Pháp"),
    CHINESE("Chinese", "Tiếng Trung"),
    JAPANESE("Japanese", "Tiếng Nhật"),
    ITALIAN("Italian", "Tiếng Ý"),
    SPANISH("Spanish", "Tiếng Tây Ban Nha"),
    RUSSIAN("Russian", "Tiếng Nga"),
    POLISH("Polish", "Tiếng Ba Lan"),
    MANDARIN("Mandarin", "Tiếng Quan Thoại"),
    LATIN("Latin", "Tiếng Latinh"),
    CZECH("Czech", "Tiếng Séc"),
    CANTONESE("Cantonese", "Tiếng Quảng Đông"),
    HUNGARIAN("Hungarian", "Tiếng Hungary"),
    THAI("Thai", "Tiếng Thái"),
    INDONESIAN("Indonesian", "Tiếng Indonesia");

    private final String value;
    private final String description;

    public static MovieLanguage fromValue(String value) {
        for (MovieLanguage lang : values()) {
            if (lang.value.equalsIgnoreCase(value)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Invalid language value: " + value);
    }
}

