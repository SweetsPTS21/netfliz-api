package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    IUserRepository userRepository;

    public UserValidator(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserExist(Long userId) {
        if (userRepository.existsById(userId)) {
            return;
        }
        throw new NotFoundException("User not found");
    }
}
