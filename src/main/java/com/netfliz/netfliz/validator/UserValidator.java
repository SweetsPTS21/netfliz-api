package com.netfliz.netfliz.validator;

import com.netfliz.netfliz.exception.BadRequestException;
import com.netfliz.netfliz.exception.NotFoundException;
import com.netfliz.netfliz.repository.IUserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserValidator {
    IUserRepository userRepository;

    public UserValidator(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserExist(Long userId) {
        if (Objects.nonNull(userId) && userRepository.existsById(userId)) {
            return;
        }
        throw new NotFoundException("User not found");
    }

    public void validateUserEmail(String email) {
        if (Strings.isNotBlank(email) && userRepository.findByEmail(email).isEmpty()) {
            return;
        }
        throw new BadRequestException("User email has been registered");
    }
}
