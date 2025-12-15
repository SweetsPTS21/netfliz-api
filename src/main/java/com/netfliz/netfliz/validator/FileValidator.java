package com.netfliz.netfliz.validator;

import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class FileValidator {
    private final Tika tika = new Tika();
    private static final long MAX_IMAGE_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private static final long MAX_ASSET_FILE_SIZE = 10 * 1024 * 1024; // 10MB in bytes
    private static final long MAX_VIDEO_FILE_SIZE = 500 * 1024 * 1024; // 500MB in bytes
    private static final long MAX_COMMON_FILE_SIZE = 50 * 1024 * 1024; // 50MB in bytes
    private static final List<String> FILE_FORMAT_SUPPORT = List.of("jpeg", "jpg", "png");
    private static final List<String> MOVIE_TYPE = List.of("movies", "trailers");

    public void validateImage(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File không được để trống!");
        }

        // Check file size
        if (file.getSize() > MAX_IMAGE_FILE_SIZE) {
            throw new ValidationException("Kích thước file không được vượt quá 5MB");
        }

        String fileType;
        try {
            fileType = tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new ValidationException("Lỗi khi đọc file file: " + e.getMessage());
        }

        if (!fileType.startsWith("image/")) {
            throw new ValidationException("File không phải ảnh.");
        }

        // Chỉ support file jpeg/jpg/png
        String ext = fileType.split("/")[1];
        if (!FILE_FORMAT_SUPPORT.contains(ext)) {
            throw new ValidationException("Chỉ hỗ trợ định dạng jpeg/jpg/png");
        }
    }

    public void validateAsset(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File không được để trống!");
        }

        // Check file size
        if (file.getSize() > MAX_ASSET_FILE_SIZE) {
            throw new ValidationException("Kích thước file không được vượt quá 10MB");
        }
    }

    public void validateVideo(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File không được để trống!");
        }

        // Check file size
        if (file.getSize() > MAX_VIDEO_FILE_SIZE) {
            throw new ValidationException("Kích thước file không được vượt quá 10MB");
        }
    }

    public void validateType(String type) {
        if (Strings.isBlank(type)) {
            throw new ValidationException("Type không được để trống!");
        }

        if (!MOVIE_TYPE.contains(type)) {
            throw new ValidationException("Type không hợp lệ!");
        }
    }

    public void validateFile(MultipartFile file, String ext) {
        if (Objects.isNull(file)) {
            throw new ValidationException("Cần truyền lên file định dạng " + ext);
        }

        String fileExt = Optional.ofNullable(file.getOriginalFilename())
                .map((extension) -> extension.split("\\.")[1])
                .orElse(null);
        if (!Objects.equals(fileExt, ext)) {
            throw new ValidationException("Chỉ hỗ trợ định dạng " + ext);
        }
    }

    public void validateCommonFile(MultipartFile file) {
        if (Objects.isNull(file)) {
            throw new ValidationException("File không hợp lệ");
        }

        if (file.getSize() > MAX_COMMON_FILE_SIZE) {
            throw new ValidationException("Hỗ trợ file tối đa 50MB");
        }
    }
}
