package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.UsersApiDelegate;
import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.mapper.ProfileMapper;
import com.netfliz.netfliz.mapper.UserMapper;
import com.netfliz.netfliz.model.Profile;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.model.UserPage;
import com.netfliz.netfliz.repository.IProfileRepository;
import com.netfliz.netfliz.repository.IUserRepository;
import com.netfliz.netfliz.validator.UserValidator;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Primary
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserPage> getAllUser(Integer page, Integer pageSize, String filter, String sort) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<UserEntity> userPage = userRepository.findAll(pageable);

        return ResponseEntity.ok(buildUserPage(userPage));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(Long userId) {
        userValidator.validateUserExist(userId);
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userMapper.mapUserEntityToUser(userRepository.findById(userId).get());
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<List<Profile>> getAllProfileByUserId(Long userId) {
        userValidator.validateUserExist(userId);
        Optional<List<ProfileEntity>> profileOptional = profileRepository.findByUserId(userId.intValue());

        if (profileOptional.isPresent()) {
            List<Profile> profiles = profileMapper.mapProfileEntityListToProfileList(profileOptional.get());
            return ResponseEntity.ok(profiles);
        }

        return ResponseEntity.ok(new ArrayList<>());
    }

    @Override
    public ResponseEntity<Profile> getProfileByUserIdAndProfileId(Long userId, Long profileId) {
        userValidator.validateUserExist(userId);
        Optional<ProfileEntity> profileOptional = profileRepository.findByUserIdAndProfileId(userId, profileId);

        if (profileOptional.isPresent()) {
            Profile profile = profileMapper.mapProfileEntityToProfile(profileRepository.findById(profileId).get());
            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.ok(new Profile());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(User user) {
        UserEntity userEntity = userMapper.mapUserToUserEntity(user);
        userRepository.save(userEntity);

        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(Long userId, User user) {
        userValidator.validateUserExist(userId);
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        UserEntity userEntity = userMapper.mapUserToUserEntity(user);
        return ResponseEntity.ok(userMapper.mapUserEntityToUser(userRepository.save(userEntity)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(Long userId) {
        userValidator.validateUserExist(userId);
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    private UserPage buildUserPage(Page<UserEntity> userPage) {
        UserPage userPageResponse = new UserPage();

        userPageResponse.setPage(userPage.getNumber());
        userPageResponse.setPageSize(userPage.getSize());
        userPageResponse.setTotal((int) userPage.getTotalElements());
        userPageResponse.setTotalPages(userPage.getTotalPages());
        userPageResponse.setItems(userMapper.mapUserEntityListToUserList(userPage.getContent()));

        return userPageResponse;
    }
}
