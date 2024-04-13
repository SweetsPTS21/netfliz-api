package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.UserApiDelegate;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.mapper.UserMapper;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.repository.IUserRepository;
import com.netfliz.netfliz.validator.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserApiDelegate {
    IUserRepository userRepository;

    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public UserService(IUserRepository userRepository ,UserValidator userValidator, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userMapper.mapUserEntityListToUserList(userRepository.findAll());
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<User> getUserById(String userId) {
        userValidator.validateUserExist(userId);

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userMapper.mapUserEntityToUser(userRepository.findById(userId).get());
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> createUser(User user) {
        UserEntity userEntity = userMapper.mapUserToUserEntity(user);
        userRepository.save(userEntity);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<Void> updateUser(String userId, User user) {
        userValidator.validateUserExist(userId);

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity userEntity = userMapper.mapUserToUserEntity(user);
        userRepository.save(userEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userValidator.validateUserExist(userId);

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }


}
