package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.entity.enums.UserStatus;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.role.Role;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity mapUserToUserEntity(User from, UserEntity to) {
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStatus(UserStatus.valueOf(from.getStatus().getValue()));
        to.setRole(Role.valueOf(from.getRole().getValue()));

        if (Strings.isNotBlank(from.getPassword())) {
            to.setPassword(passwordEncoder.encode(from.getPassword()));
        }

        return to;
    }

    public User mapUserEntityToUser(UserEntity from) {
        User to = new User();
        to.setId(from.getId());
        to.setUsername(from.getUsername());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStatus(User.StatusEnum.fromValue(from.getStatus().getName()));
        to.setRole(User.RoleEnum.fromValue(from.getRole().getName()));
        to.setCreatedAt(from.getCreatedAt().atOffset(ZoneOffset.UTC));
        to.setUpdatedAt(from.getUpdatedAt().atOffset(ZoneOffset.UTC));

        return to;
    }

    public List<User> mapUserEntityListToUserList(List<UserEntity> from) {
        return from.stream().map(this::mapUserEntityToUser).toList();
    }
}
