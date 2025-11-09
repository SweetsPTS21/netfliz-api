package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.model.UserPage;
import com.netfliz.netfliz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> createUser(User user) {
        return userService.createUser(user);
    }

    @Override
    public ResponseEntity<User> updateUser(Long userId, User user) {
        return userService.updateUser(userId, user);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        return userService.deleteUser(userId);
    }

    @Override
    public ResponseEntity<UserPage> getAllUser(Integer page, Integer pageSize, String filter, String sort) {
        return userService.getAllUser(page, pageSize, filter, sort);
    }

    @Override
    public ResponseEntity<User> getUserById(Long userId) {
        return userService.getUserById(userId);
    }
}
