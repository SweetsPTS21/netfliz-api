package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.service.ProfileService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3008", allowedHeaders = "*", allowCredentials = "true")
public class ProfileGraphql {

    private final ProfileService profileService;

    public ProfileGraphql(ProfileService profileService) {
        this.profileService = profileService;
    }

    @QueryMapping
    public List<Profile> getAllProfile() {
        ResponseEntity<List<Profile>> responseEntity = profileService.getAllProfile();
        return responseEntity.getBody();
    }

    @QueryMapping
    public Profile getProfileById(@Argument String profileId) {
        ResponseEntity<Profile> responseEntity = profileService.getProfileById(profileId);
        return responseEntity.getBody();
    }

    @MutationMapping
    public Profile createProfile(@Argument Profile profile) {
        ResponseEntity<Profile> responseEntity = profileService.createProfile(profile);
        return responseEntity.getBody();
    }

    @MutationMapping
    public boolean updateProfile(@Argument String profileId, @Argument Profile profile) {
        ResponseEntity<Void> responseEntity = profileService.updateProfile(profileId, profile);
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @MutationMapping
    public void deleteProfile(@Argument String profileId) {
        ResponseEntity<Void> responseEntity = profileService.deleteProfile(profileId);

    }
}
