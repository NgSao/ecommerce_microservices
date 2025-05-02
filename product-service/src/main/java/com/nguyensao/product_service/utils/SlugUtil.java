package com.nguyensao.product_service.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtil {
    public static String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{M}", "") // bỏ dấu
                .toLowerCase(Locale.ROOT) // viết thường
                .replaceAll("[^a-z0-9\\s-]", "") // bỏ ký tự đặc biệt
                .replaceAll("[\\s]+", "-") // thay khoảng trắng bằng -
                .replaceAll("[-]{2,}", "-") // bỏ dấu - thừa
                .replaceAll("^-|-$", ""); // bỏ dấu - đầu/cuối
        return slug;
    }
}
