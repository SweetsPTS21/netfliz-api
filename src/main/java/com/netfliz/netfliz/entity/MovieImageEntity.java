package com.netfliz.netfliz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netfliz.netfliz.entity.converter.MovieImageTypeConverter;
import com.netfliz.netfliz.entity.enums.MovieImageObjectType;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "movie_images")
public class MovieImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_type")
    @Convert(converter = MovieImageTypeConverter.class)
    private MovieImageType imageType;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "object_id")
    @Schema(description = "Id của đối tượng")
    private Long objectId;

    @Column(name = "object_type")
    @Schema(description = "Loại đối tượng (phim/tập phim/mùa")
    private MovieImageObjectType objectType;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
}
