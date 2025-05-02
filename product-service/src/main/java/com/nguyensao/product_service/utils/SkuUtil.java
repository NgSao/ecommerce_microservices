package com.nguyensao.product_service.utils;

import java.util.Random;

public class SkuUtil {
    public static String generateSku(String name) {
        String prefix = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        prefix = prefix.length() > 6 ? prefix.substring(0, 6) : prefix;

        String timePart = String.valueOf(System.currentTimeMillis()).substring(8);
        int random = new Random().nextInt(900) + 100;

        return prefix + "-" + timePart + random;
    }
}
