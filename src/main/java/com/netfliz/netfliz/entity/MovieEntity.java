package com.netfliz.netfliz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private int year;
    private String trailer;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String categories;
    private String director;
    private String writer;
    private String actors;
    @Column(columnDefinition = "TEXT")
    private String plot;
    private String languages;
    private String country;
    private String awards;
    @Column(columnDefinition = "bigint default 0")
    private long posterId;
    private Long metaScore;
    private String imdbRating;
    private Long imdbVotes;
    private String type;
    private boolean response;
    @Column(columnDefinition = "TEXT")
    private String images;
    private Date createdDate;
    private Date updatedDate;
}
