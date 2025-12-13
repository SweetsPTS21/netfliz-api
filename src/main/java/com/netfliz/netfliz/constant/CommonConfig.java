package com.netfliz.netfliz.constant;

public class CommonConfig {
    public static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/graphiql/**",
            "/api/v1/file/presign-url",
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/api/v1/auth/refresh-token",
            "/api/v1/file/presign-url",
            "/api/v1/utils/mermaid-file/**"
    };
}
