package com.netfliz.netfliz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.netfliz.netfliz.entity.converter.MovieAssetTypeConverter;
import com.netfliz.netfliz.entity.enums.MovieAssetType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Data
@Entity
@Table(name = "movie_assets")
public class MovieAssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "episode_id")
    private Long episodeId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "asset_type")
    @Convert(converter = MovieAssetTypeConverter.class)
    private MovieAssetType assetType;

    @Column(name = "name")
    private String name;

    @Column(name = "format")
    private String format;

    @Column(name = "url")
    private String url;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode drm;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode rendition;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;
}
