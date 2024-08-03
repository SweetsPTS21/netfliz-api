package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.service.ProfileService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ProfileController implements ProfilesApi {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public ResponseEntity<List<Profile>> getAllProfile() {
        return profileService.getAllProfile();
    }

    @Override
    public ResponseEntity<Profile> getProfileById(String profileId) {
        return profileService.getProfileById(profileId);
    }

    @Override
    public ResponseEntity<Profile> createProfile(Profile profile) {
        return profileService.createProfile(profile);
    }

    @Override
    public ResponseEntity<Void> updateProfile(String profileId, Profile profile) {
        return profileService.updateProfile(profileId, profile);
    }

    @Override
    public ResponseEntity<Void> deleteProfile(String profileId) {
        return profileService.deleteProfile(profileId);
    }
}
