package com.netfliz.netfliz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Data
@Entity
@Table(name = "movie_episodes")
public class MovieEpisodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    @Column(name = "episode_order")
    private Integer episodeOrder;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "runtime")
    private Integer runtime;

    @Column(name = "is_published")
    private Boolean isPublished;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;

    @Column(name = "updated_at", insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
}
