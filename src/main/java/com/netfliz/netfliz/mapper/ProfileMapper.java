package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.entity.enums.ProfileStatus;
import com.netfliz.netfliz.entity.enums.ProfileType;
import com.netfliz.netfliz.model.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public ProfileEntity mapProfileToProfileEntity(Profile from, UserEntity user) {
        ProfileEntity to = new ProfileEntity();
        if (from.getId() != null) {
            to.setId(from.getId());
        }

        to.setName(from.getName());
        to.setAvatar(from.getAvatar());
        to.setStatus(ProfileStatus.valueOf(from.getStatus().getValue()));
        to.setType(ProfileType.valueOf(from.getType()));
        to.setPassword(from.getPassword());
        to.setDescription(from.getDescription());
        to.setUser(user);
        return to;
    }

    public Profile mapProfileEntityToProfile(ProfileEntity profileEntity) {
        Profile to = new Profile();
        to.setId(profileEntity.getId());
        to.setName(profileEntity.getName());
        to.setAvatar(profileEntity.getAvatar());
        to.setStatus(Profile.StatusEnum.valueOf(profileEntity.getStatus().getName()));
        to.setType(String.valueOf(profileEntity.getType()));
        to.setPassword(profileEntity.getPassword());
        to.setDescription(profileEntity.getDescription());
        to.setUserId(profileEntity.getUser().getId());
        return to;
    }

    public List<Profile> mapProfileEntityListToProfileList(List<ProfileEntity> from) {
        return from.stream().map(this::mapProfileEntityToProfile).toList();
    }

    public List<ProfileEntity> mapProfileListToProfileEntityList(List<Profile> from, UserEntity user) {
        return from.stream().map(profile -> mapProfileToProfileEntity(profile, user)).toList();
    }
}
