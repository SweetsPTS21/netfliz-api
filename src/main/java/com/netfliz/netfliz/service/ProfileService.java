package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.ProfilesApiDelegate;
import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.mapper.ProfileMapper;
import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.repository.IProfileRepository;
import com.netfliz.netfliz.validator.ProfileValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService implements ProfilesApiDelegate {
    IProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final ProfileValidator profileValidator;

    public ProfileService(IProfileRepository profileRepository ,ProfileMapper profileMapper, ProfileValidator profileValidator) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.profileValidator = profileValidator;
    }

    @Override
    public ResponseEntity<List<Profile>> getAllProfile() {
        List<Profile> profiles = profileMapper.mapProfileEntityListToProfileList(profileRepository.findAll());
        return ResponseEntity.ok(profiles);
    }

    @Override
    public ResponseEntity<Profile> getProfileById(Long profileId) {
        profileValidator.validateProfileExist(profileId);

        Optional<ProfileEntity> profileOptional = profileRepository.findById(profileId);

        if (profileOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Profile profile = profileMapper.mapProfileEntityToProfile(profileRepository.findById(profileId).get());
        return ResponseEntity.ok(profile);

    }

    @Override
    public ResponseEntity<Profile> createProfile(Profile profile) {
        ProfileEntity profileEntity = profileRepository.save(profileMapper.mapProfileToProfileEntity(profile));
        return ResponseEntity.ok(profileMapper.mapProfileEntityToProfile(profileEntity));
    }

    @Override
    public ResponseEntity<Void> updateProfile(Long profileId, Profile profile) {
        profileValidator.validateProfileExist(profileId);

        Optional<ProfileEntity> profileOptional = profileRepository.findById(profileId);

        if (profileOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProfileEntity profileEntity = profileMapper.mapProfileToProfileEntity(profile);
        profileEntity.setId(Math.toIntExact(profileId));
        profileRepository.save(profileEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteProfile(Long profileId) {
        profileValidator.validateProfileExist(profileId);

        Optional<ProfileEntity> profileOptional = profileRepository.findById(profileId);

        if (profileOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        profileRepository.deleteById(profileId);
        return ResponseEntity.ok().build();
    }
}
