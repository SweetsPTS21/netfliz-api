package com.netfliz.netfliz.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity implements UserDetails {
    @Id
    private String id;
    @TextIndexed
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    @TextIndexed
    private String email;
    @TextIndexed
    private String phone;
    private String avatar;
    private int status;
    private String type;
    private String role;

    public UserEntity() {
    }

    public UserEntity(String id, String username, String password, String email, String phone, String avatar, int status, String type, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.status = status;
        this.type = type;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
