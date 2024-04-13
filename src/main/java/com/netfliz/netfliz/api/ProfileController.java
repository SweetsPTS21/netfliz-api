package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.service.ProfileService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ProfileController implements ProfileApi {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public ResponseEntity<List<Profile>> getAllProfile() {
        return ProfileApi.super.getAllProfile();
    }

    @Override
    public ResponseEntity<Profile> getProfileById(Long profileId) {
        return ProfileApi.super.getProfileById(profileId);
    }

    @Override
    public ResponseEntity<Profile> createProfile(Profile profile) {
        return ProfileApi.super.createProfile(profile);
    }

    @Override
    public ResponseEntity<Void> updateProfile(Long profileId, Profile profile) {
        return ProfileApi.super.updateProfile(profileId, profile);
    }

    @Override
    public ResponseEntity<Void> deleteProfile(Long profileId) {
        return ProfileApi.super.deleteProfile(profileId);
    }
}
