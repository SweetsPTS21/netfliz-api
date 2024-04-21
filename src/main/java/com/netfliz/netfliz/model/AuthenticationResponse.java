package com.netfliz.netfliz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netfliz.netfliz.entity.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("accessToken")
  private String accessToken;
  @JsonProperty("refreshToken")
  private String refreshToken;
  @JsonProperty("tokenType")
  private TokenType tokenType;
}
