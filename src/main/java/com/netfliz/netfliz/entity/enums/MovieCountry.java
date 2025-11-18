package com.netfliz.netfliz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieCountry {
    USA("USA", "Hoa Kỳ"),
    UK("UK", "Vương quốc Anh"),
    CANADA("Canada", "Canada"),
    JAPAN("Japan", "Nhật Bản"),
    CHINA("China", "Trung Quốc"),
    VIETNAM("VietNam", "Việt Nam"),
    AUSTRALIA("Australia", "Úc"),
    NEW_ZEALAND("New Zealand", "New Zealand"),
    INDIA("India", "Ấn Độ"),
    SOUTH_AFRICA("South Africa", "Nam Phi"),
    SOUTH_KOREA("South Korea", "Hàn Quốc"),
    HONG_KONG("Hong Kong", "Hồng Kông"),
    FRANCE("France", "Pháp"),
    BRAZIL("Brazil", "Brazil"),
    GERMANY("Germany", "Đức"),
    ITALY("Italy", "Ý"),
    SPAIN("Spain", "Tây Ban Nha"),
    RUSSIA("Russia", "Nga"),
    POLAND("Poland", "Ba Lan");

    private final String value;
    private final String description;


    public static MovieCountry fromValue(String value) {
        for (MovieCountry c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid country value: " + value);
    }
}


