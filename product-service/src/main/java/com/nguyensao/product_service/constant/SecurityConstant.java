package com.nguyensao.product_service.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class SecurityConstant {
    public static final String[] PUBLIC_URLS = {
            "api/v1/products/public/**",
            "/v3/api-docs/**", "/swagger-ui/**",
            "/swagger-ui.html" };
    public static final String[] ADMIN_URLS = { "api/v1/products/admin/**" };
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public static final String INVALID_TOKEN = "Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header).";
    public static final String ACCESS_DENIED = "Bạn không có quyền truy cập tài nguyên này.";

}
