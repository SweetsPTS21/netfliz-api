package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.UsersApiDelegate;
import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.mapper.ProfileMapper;
import com.netfliz.netfliz.mapper.UserMapper;
import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.repository.IProfileRepository;
import com.netfliz.netfliz.repository.IUserRepository;
import com.netfliz.netfliz.validator.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UsersApiDelegate {
    IUserRepository userRepository;
    IProfileRepository profileRepository;

    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    public UserService(IUserRepository userRepository , IProfileRepository profileRepository, UserValidator userValidator, UserMapper userMapper, ProfileMapper profileMapper) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
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
    public ResponseEntity<List<Profile>> getAllProfileByUserId(String userId) {
        userValidator.validateUserExist(userId);

        Optional<List<ProfileEntity>> profileOptional = profileRepository.findByUserId(userId);

        if (profileOptional.isPresent()) {
            List<Profile> profiles = profileMapper.mapProfileEntityListToProfileList(profileOptional.get());
            return ResponseEntity.ok(profiles);
        }

        return ResponseEntity.ok(new ArrayList<>());
    }

    @Override
    public ResponseEntity<Profile> getProfileByUserIdAndProfileId(String userId, String profileId) {
        userValidator.validateUserExist(userId);

        Optional<ProfileEntity> profileOptional = profileRepository.findByUserIdAndProfileId(userId, profileId);

        if (profileOptional.isPresent()) {
            Profile profile = profileMapper.mapProfileEntityToProfile(profileRepository.findById(profileId).get());
            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.ok(new Profile());
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
