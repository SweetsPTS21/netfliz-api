package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.repository.IProfileRepository;
import org.springframework.stereotype.Component;

@Component
public class ProfileValidator {
    IProfileRepository profileRepository;

    public ProfileValidator(IProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public void validateProfileExist(Long profileId) {
        if (profileRepository.existsById(profileId)) {
            return;
        }
        throw new NotFoundException("Movie does not exist");
    }
}
