package com.netfliz.netfliz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netfliz.netfliz.config.JwtService;
import com.netfliz.netfliz.entity.*;
import com.netfliz.netfliz.exception.BadCredentialException;
import com.netfliz.netfliz.mapper.UserMapper;
import com.netfliz.netfliz.model.AuthenticationRequest;
import com.netfliz.netfliz.model.AuthenticationResponse;
import com.netfliz.netfliz.model.RegisterRequest;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.repository.IProfileRepository;
import com.netfliz.netfliz.repository.ITokenRepository;
import com.netfliz.netfliz.repository.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsChecker {
    private final IUserRepository userRepository;
    private final ITokenRepository tokenRepository;
    private final IProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(String.valueOf(request.getRole()))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        // create profile for user
        var profile = ProfileEntity.builder()
                .userId(savedUser.getId())
                .name("Default")
                .description("Default profile")
                .status("active")
                .type(ProfileType.DEFAULT)
                .build();

        profileRepository.save(profile);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialException("Invalid email/password"));

        check(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialException("Invalid email/password");
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }

    public ResponseEntity<AuthenticationResponse> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        final String authHeader = request.getHeader("Cookie");

        if (authHeader == null) {
            throw new BadCredentialException("Invalid token");
        }

        List<String> cookies = Arrays.asList(authHeader.split(";"));
        final String jwt = cookies.stream()
            .filter(cookie -> cookie.contains("accessToken"))
            .findFirst()
            .orElseThrow(() -> new BadCredentialException("Invalid token"))
            .split("=")[1];

        var storedToken = tokenRepository.findByToken(jwt)
            .orElseThrow(() -> new BadCredentialException("Invalid token"));

        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }

        return ResponseEntity.ok().build();
    }

    public AuthenticationResponse getUserToken(User user) {

        var findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BadCredentialException("User not found"));

        var validUserTokens = tokenRepository.findAllValidTokenByUser(findUser.getId());
        if (validUserTokens.isEmpty())
            return null;

        var token = validUserTokens.get(0);

        return AuthenticationResponse.builder()
                .accessToken(token.getToken())
                .refreshToken(token.getToken())
                .tokenType(TokenType.BEARER)
                .build();

    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public User getMe(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.COOKIE);
        final String accessToken;
        final String userEmail;

        if (authHeader == null) {
            return null;
        }
        Map<String, String> cookies = new HashMap<>();

        for (String cookiePair : authHeader.split(";")) {
            String[] keyValue = cookiePair.trim().split("=");
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }

        accessToken = cookies.get("accessToken");
        userEmail = jwtService.extractUsername(accessToken);

        if (userEmail != null) {
            UserEntity userEntity = userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new BadCredentialException("User not found")
            );
            return userMapper.mapUserEntityToUser(userEntity);
        }
        return null;
    }

    @Override
    public void check(UserDetails toCheck) {
        if (!toCheck.isAccountNonLocked()) {
            throw new BadCredentialException("User is locked");
        }
        if (!toCheck.isAccountNonExpired()) {
            throw new BadCredentialException("Account is expired");
        }
        if (!toCheck.isCredentialsNonExpired()) {
            throw new BadCredentialException("Credentials are expired");
        }
        if (!toCheck.isEnabled()) {
            throw new BadCredentialException("User is disabled");
        }
    }
}
