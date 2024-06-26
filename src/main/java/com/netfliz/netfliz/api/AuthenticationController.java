package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.AuthenticationRequest;
import com.netfliz.netfliz.model.AuthenticationResponse;
import com.netfliz.netfliz.service.AuthenticationService;
import com.netfliz.netfliz.model.RegisterRequest;
import com.netfliz.netfliz.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3008", allowedHeaders = "*", allowCredentials = "true")
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
       return service.logout(request, response);
    }

  @PostMapping("/token")
  public ResponseEntity<AuthenticationResponse> getToken(
          @RequestBody User user
          ) {
    return ResponseEntity.ok(service.getUserToken(user));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/me")
  public User getMe(HttpServletRequest request) {
    return service.getMe(request);
  }


}
