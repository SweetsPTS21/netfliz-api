package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.entity.ProfileType;
import com.netfliz.netfliz.model.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public ProfileEntity mapProfileToProfileEntity(Profile from) {
        ProfileEntity to = new ProfileEntity();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setAvatar(from.getAvatar());
        to.setStatus(from.getStatus());
        to.setType(ProfileType.valueOf(from.getType()));
        to.setPassword(from.getPassword());
        to.setDescription(from.getDescription());
        to.setUserId(from.getUserId());
        return to;
    }

    public Profile mapProfileEntityToProfile(ProfileEntity profileEntity) {
        Profile to = new Profile();
        to.setId(profileEntity.getId());
        to.setName(profileEntity.getName());
        to.setAvatar(profileEntity.getAvatar());
        to.setStatus(profileEntity.getStatus());
        to.setType(String.valueOf(profileEntity.getType()));
        to.setPassword(profileEntity.getPassword());
        to.setDescription(profileEntity.getDescription());
        to.setUserId(profileEntity.getUserId());
        return to;
    }

    public List<Profile> mapProfileEntityListToProfileList(List<ProfileEntity> from) {
        return from.stream().map(this::mapProfileEntityToProfile).toList();
    }

    public List<ProfileEntity> mapProfileListToProfileEntityList(List<Profile> from) {
        return from.stream().map(this::mapProfileToProfileEntity).toList();
    }
}
