package com.netfliz.netfliz.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
public class ProfileEntity {
    @Id
    private String id;
    @TextIndexed
    private String name;
    private String avatar;
    @TextIndexed
    private String status;
    private String type;
    private String password;
    private String description;
    private String userId;

    public ProfileEntity() {
    }

    public ProfileEntity(String id, String name, String avatar, String status, String type, String password, String description, String userId) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.status = status;
        this.type = type;
        this.password = password;
        this.description = description;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
