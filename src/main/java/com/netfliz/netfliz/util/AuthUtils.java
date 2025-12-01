package com.netfliz.netfliz.util;

import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.repository.UserRepository;
import com.netfliz.netfliz.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@AllArgsConstructor
public class AuthUtils {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Lấy entity User đang đăng nhập
     */
    public UserEntity getCurrentUser() {
        // 1) thử lấy từ security context trước
        String username = getPrincipalFromSecurityContext();
        if (username == null) {
            // 2) fallback: lấy từ JWT trong request
            username = getUsernameFromJwtInRequest();
        }

        if (username == null) {
            return null; // hoặc throw
        }

        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Lấy userId nhanh
     */
    public Long getCurrentUserId() {
        UserEntity u = getCurrentUser();
        return u != null ? u.getId() : null;
    }

    /**
     * Lấy email nhanh
     */
    public String getCurrentUserEmail() {
        UserEntity u = getCurrentUser();
        return u != null ? u.getEmail() : null;
    }

    private String getPrincipalFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }
        if (principal instanceof String s) {
            return s;
        }
        return null;
    }

    private String getUsernameFromJwtInRequest() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) return null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtService.extractUsername(token);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
