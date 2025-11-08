package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.model.UserPage;
import com.netfliz.netfliz.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserGraphql {
    private final UserService userService;

    public UserGraphql(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<Profile> getAllProfileByUserId(@Argument Long id) {
        ResponseEntity<List<Profile>> responseEntity = userService.getAllProfileByUserId(id);
        return responseEntity.getBody();
    }

    @QueryMapping
    public UserPage getAllUser(@Argument int page, @Argument int pageSize, @Argument String filter, @Argument String sort) {
        ResponseEntity<UserPage> responseEntity = userService.getAllUser(page, pageSize, filter, sort);
        return responseEntity.getBody();
    }

    @QueryMapping
    public User getUserById(@Argument Long id) {
        ResponseEntity<User> responseEntity = userService.getUserById(id);
        return responseEntity.getBody();
    }

    @MutationMapping
    public User createUser(@Argument User user) {
        ResponseEntity<User> responseEntity = userService.createUser(user);
        return responseEntity.getBody();
    }

    @MutationMapping
    public User updateUserById(@Argument Long id, @Argument User user) {
        ResponseEntity<User> responseEntity = userService.updateUser(id, user);
        return responseEntity.getBody();
    }

    @MutationMapping
    public void deleteUserById(@Argument Long id) {
        userService.deleteUser(id);
    }
}
