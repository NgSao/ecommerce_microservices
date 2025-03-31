package com.nguyensao.product_service.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidation {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    public static boolean isValidImage(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
