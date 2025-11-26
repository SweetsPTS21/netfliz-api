package com.netfliz.netfliz.service;

import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.ProfileEntity;
import com.netfliz.netfliz.entity.TokenEntity;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.entity.enums.*;
import com.netfliz.netfliz.exception.BadCredentialException;
import com.netfliz.netfliz.exception.BadRequestException;
import com.netfliz.netfliz.mapper.UserMapper;
import com.netfliz.netfliz.model.User;
import com.netfliz.netfliz.model.request.AuthenticationRequest;
import com.netfliz.netfliz.model.request.RegisterRequest;
import com.netfliz.netfliz.model.response.AuthenticationResponse;
import com.netfliz.netfliz.repository.IProfileRepository;
import com.netfliz.netfliz.repository.ITokenRepository;
import com.netfliz.netfliz.repository.UserRepository;
import com.netfliz.netfliz.util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsChecker {
    private final UserRepository userRepository;
    private final ITokenRepository tokenRepository;
    private final IProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RedisService redisService;

    public AuthenticationResponse register(RegisterRequest request) {
        request.validate();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }

        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .type(UserType.COMMON)
                .username(CommonUtils.generateUsername(request.getEmail()))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        // Cache user info
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_USER_INFO, savedUser.getUsername());
        redisService.set(cacheKey, savedUser);

        // create profile for user
        var profile = ProfileEntity.builder()
                .user(savedUser)
                .name("Default")
                .description("Default profile")
                .status(ProfileStatus.ACTIVE)
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

        // Check user lock status
        check(user);

        // Check status
        checkEntity(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialException("Invalid email/password");
        }

        // Cache user info
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_USER_INFO, user.getUsername());
        redisService.set(cacheKey, user);

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
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialException("Invalid token");
        }
        final String refreshToken = authHeader.substring(7);

        var storedToken = tokenRepository.findByToken(refreshToken)
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
        var token = TokenEntity.builder()
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

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialException("Invalid token");
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        throw new BadCredentialException("Invalid token");
    }

    public User getMe(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken;
        final String username;

        accessToken = authHeader.substring(7);
        username = jwtService.extractUsername(accessToken);

        if (username != null) {
            String cacheKey = CacheKey.buildKey(CacheKey.CACHE_USER_INFO, username);
            var user = redisService.get(cacheKey, UserEntity.class);
            if (user != null) {
                return userMapper.mapUserEntityToUser(user);
            }
            UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                    () -> new BadCredentialException("User not found")
            );
            redisService.set(cacheKey, userEntity);

            return userMapper.mapUserEntityToUser(userEntity);
        }
        return null;
    }

    public static void checkEntity(UserEntity entity) {
        if (entity.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialException("User is temporary unavailable!");
        }
    }

    @Override
    public void check(UserDetails toCheck) {
        if (!toCheck.isAccountNonLocked()) {
            throw new LockedException("User is locked");
        }
        if (!toCheck.isAccountNonExpired()) {
            throw new AccountExpiredException("Account is expired");
        }
        if (!toCheck.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Credentials are expired");
        }
        if (!toCheck.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
    }
}
