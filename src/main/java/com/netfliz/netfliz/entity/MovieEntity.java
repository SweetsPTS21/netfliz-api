package com.netfliz.netfliz.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movies")
public class MovieEntity {
    @Id
    private String id;
    @TextIndexed
    private String title;
    private int year;
    private String trailer;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private List<String> categories;
    @TextIndexed
    private String director;
    private String writer;
    @TextIndexed
    private String actors;
    private String plot;
    private String languages;
    @TextIndexed
    private String country;
    private String awards;
    private String poster;
    private Long metascore;
    private String imdbRating;
    private Long imdbVotes;
    private String type;
    private boolean response;
    private List<String> images;
    private Date createdDate;
    private Date updatedDate;

}
