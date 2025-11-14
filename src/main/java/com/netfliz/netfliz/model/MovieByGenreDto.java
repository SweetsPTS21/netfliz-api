package com.netfliz.netfliz.model;

import com.netfliz.netfliz.entity.MovieEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovieByGenreDto extends MovieEntity {
    private String name;
    private Integer rowNumber;
}
