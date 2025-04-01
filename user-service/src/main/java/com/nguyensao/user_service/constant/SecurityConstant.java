package com.nguyensao.user_service.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class SecurityConstant {
    // 1 day (seconds = 24 * 60 * 60)
    public static final long EXPIRATION_TIME = (24 * 60 * 60) * 2;
    public static final long REFRESH_TOKEN_EXP = (24 * 60 * 60) * 12;
    public static final String[] PUBLIC_URLS = { "api/v1/users/public/login", "api/v1/users/public/register",
            "api/v1/users/public/verify", "api/v1/users/public/forgot-password", "api/v1/users/public/activate",
            "api/v1/users/image/**",
            "api/v1/users/resetpassword/**", "api/v1/users/refresh", "/v3/api-docs/**", "/swagger-ui/**",
            "/swagger-ui.html" };
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public static long EXPIRATION_OTP = 3 * 60; // 3 phút (3 phút * 60 giây)
}
