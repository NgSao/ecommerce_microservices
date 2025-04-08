package com.nguyensao.product_service.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class SecurityConstant {
    public static final String[] PUBLIC_URLS = { "api/v1/products/public/test1", "api/v1/users/products/register",
            "/v3/api-docs/**", "/swagger-ui/**",
            "/swagger-ui.html" };
    public static final String[] ADMIN_URLS = { "api/v1/products/admin/**" };
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
}
