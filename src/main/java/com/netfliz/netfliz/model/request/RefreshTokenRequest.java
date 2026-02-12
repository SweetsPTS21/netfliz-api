package com.netfliz.netfliz.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Không để trống refresh token")
    private String refreshToken;
}
