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
import com.netfliz.netfliz.repository.ITokenRepository;
import com.netfliz.netfliz.repository.UserRepository;
import com.netfliz.netfliz.util.CommonUtils;
import com.netfliz.netfliz.validator.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@AllArgsConstructor
public class UserService implements UsersApiDelegate {
    UserRepository userRepository;
    IProfileRepository profileRepository;
    ITokenRepository tokenRepository;

    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

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

    /**
     * Lấy tất cả profile của user, dùng trên màn profile
     * Không phân quyền
     */
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @Transactional
    public ResponseEntity<User> createUser(User user) {
        userValidator.validateUserEmail(user.getEmail());

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(CommonUtils.generateUsername(user.getEmail()));
        userRepository.save(userMapper.mapUserToUserEntity(user, userEntity));

        return ResponseEntity.ok(userMapper.mapUserEntityToUser(userEntity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<User> updateUser(Long userId, User user) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        userMapper.mapUserToUserEntity(user, userOptional.get());

        return ResponseEntity.ok(userMapper.mapUserEntityToUser(userRepository.save(userOptional.get())));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteUser(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        // delete all token
        tokenRepository.deleteAllByUserId(userId);
        // delete all profile
        profileRepository.deleteAllByUserId(userId);
        // delete user
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
