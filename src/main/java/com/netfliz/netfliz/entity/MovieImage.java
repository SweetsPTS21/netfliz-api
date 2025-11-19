package com.netfliz.netfliz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netfliz.netfliz.entity.converter.MovieImageTypeConverter;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "movie_images")
public class MovieImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_type")
    @Convert(converter = MovieImageTypeConverter.class)
    private MovieImageType imageType;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
}
