package com.nguyensao.user_service.utils;

import java.security.SecureRandom;

public class GenerateOTP {
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
