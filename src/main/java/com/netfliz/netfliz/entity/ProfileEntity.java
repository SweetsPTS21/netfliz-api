package com.netfliz.netfliz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netfliz.netfliz.entity.converter.ProfileStatusConverter;
import com.netfliz.netfliz.entity.converter.ProfileTypeConverter;
import com.netfliz.netfliz.entity.enums.ProfileStatus;
import com.netfliz.netfliz.entity.enums.ProfileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String avatar;
    private String password;
    private String description;

    @Convert(converter = ProfileTypeConverter.class)
    private ProfileType type;

    @Convert(converter = ProfileStatusConverter.class)
    private ProfileStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_profile_user"))
    private UserEntity user;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
}
