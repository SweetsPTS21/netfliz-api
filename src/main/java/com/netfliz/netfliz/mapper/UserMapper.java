package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserEntity mapUserToUserEntity(User from) {
        UserEntity to = new UserEntity();
        to.setId(from.getId().toString());
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStatus(from.getStatus());

        return to;
    }

    public User mapUserEntityToUser(UserEntity from) {
        User to = new User();
        to.setId(Long.parseLong(from.getId()));
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStatus(from.getStatus());

        return to;
    }

    public List<User> mapUserEntityListToUserList(List<UserEntity> from) {
        return from.stream().map(this::mapUserEntityToUser).toList();
    }

    public List<UserEntity> mapUserListToUserEntityList(List<User> from) {
        return from.stream().map(this::mapUserToUserEntity).toList();
    }
}
