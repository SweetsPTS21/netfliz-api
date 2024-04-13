package com.netfliz.netfliz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
