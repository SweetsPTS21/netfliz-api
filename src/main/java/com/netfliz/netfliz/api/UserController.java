package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.service.UserService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UserController implements UsersApi{
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> createUser(User user) {
        return userService.createUser(user);
    }

    @Override
    public ResponseEntity<Void> updateUser(String userId, User user) {
        return userService.updateUser(userId, user);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        return userService.deleteUser(userId);
    }

    @Override
    public ResponseEntity<List<User>> getAllUser() {
        return userService.getAllUser();
    }

    @Override
    public ResponseEntity<User> getUserById(String userId) {
        return userService.getUserById(userId);
    }
}
