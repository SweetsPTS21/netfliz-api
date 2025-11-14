package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.request.AuthenticationRequest;
import com.netfliz.netfliz.model.response.AuthenticationResponse;
import com.netfliz.netfliz.service.AuthenticationService;
import com.netfliz.netfliz.model.request.RegisterRequest;
import com.netfliz.netfliz.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
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
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(service.refreshToken(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(HttpServletRequest request) {
        return ResponseEntity.ok(service.getMe(request));
    }

    @GetMapping("/me/authorities")
    public List<String> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return List.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
