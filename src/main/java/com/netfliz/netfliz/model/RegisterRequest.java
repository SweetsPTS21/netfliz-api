package com.netfliz.netfliz.model;

import com.netfliz.netfliz.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public void validate() {
        if (Strings.isBlank(firstname) || Strings.isBlank(lastname) || Strings.isBlank(email) || Strings.isBlank(password)) {
            throw new BadRequestException("All fields are required");
        }
    }
}
