package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    IUserRepository userRepository;

    public UserValidator(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserExist(String userId) {
        if (userRepository.existsById(userId)) {
            return;
        }
        throw new RuntimeException("User not found");
    }
}
