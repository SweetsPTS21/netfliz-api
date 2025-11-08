package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.entity.enums.UserStatus;
import com.netfliz.netfliz.model.User;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class UserMapper {
    public UserEntity mapUserToUserEntity(User from, UserEntity to) {
        to.setId(from.getId());
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStatus(UserStatus.valueOf(from.getStatus().getValue()));

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
