package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3008", allowedHeaders = "*", allowCredentials = "true")
public class UserGraphql {
    private final UserService userService;

    public UserGraphql(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<Profile> getAllProfileByUserId(@Argument String userId) {
        ResponseEntity<List<Profile>> responseEntity = userService.getAllProfileByUserId(userId);
        return responseEntity.getBody();
    }
}
