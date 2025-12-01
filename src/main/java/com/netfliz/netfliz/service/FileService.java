package com.netfliz.netfliz.service;

import com.netfliz.netfliz.entity.FileEntity;
import com.netfliz.netfliz.mapper.FileMapper;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.repository.IFileRepository;
import com.netfliz.netfliz.util.AuthUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * .
 * File service
 */
@Service
@AllArgsConstructor
public class FileService {
    private final IFileRepository fileRepository;
    private final FileMapper fileMapper;
    private final FirebaseStorageService firebaseStorageService;
    private final S3UploadService s3UploadService;
    private final AuthUtils authUtils;
    private final ImageResizerService resizer;
    private final Tika tika = new Tika();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final String UPLOAD_TYPE = "movies";
    private static final String POSTER_PATH = "poster";
    private static final String ASSET_PATH = "asset";
    private final int[] TARGET_WIDTHS = new int[]{320, 640, 1024};
    private static final long MAX_IMAGE_FILE_SIZE = 2 * 1024 * 1024; // 2MB in bytes
    private static final long MAX_ASSET_FILE_SIZE = 10 * 1024 * 1024; // 10MB in bytes
    private static final List<String> FILE_FORMAT_SUPPORT = List.of("jpeg", "jpg", "png");
    private static final List<String> MOVIE_TYPE = List.of("movies", "trailers");

    /**
     * Upload movie poster
     *
     * @param file file (jpg, png)
     * @return List<FileModel>
     */
    public List<FileModel> uploadMoviePoster(MultipartFile file) {
        validateImage(file);
        var user = authUtils.getCurrentUser();

        try {
            String fileType = tika.detect(file.getInputStream());
            if (!fileType.startsWith("image/")) {
                throw new ValidationException("File không phải ảnh.");
            }

            byte[] fileBytes = file.getBytes();
            String ext = fileType.split("/")[1]; // jpeg, png
            String outputFormat = ext.equals("jpeg") ? "jpg" : ext;
            String uuid = UUID.randomUUID().toString();
            List<FileEntity> fileEntitylList = new ArrayList<>();

            // Lưu file resize
            for (int width : TARGET_WIDTHS) {
                byte[] resized = resizer.resize(fileBytes, width, outputFormat);
                String filename = String.format("%s-%dw.%s", uuid, width, outputFormat);

                String downloadUri = uploadPosterToFirebase(resized, filename, fileType);
                fileEntitylList.add(
                        buildFileEntity(
                                file,
                                filename,
                                downloadUri,
                                String.valueOf(width),
                                user.getUsername()
                        )
                );
            }

            // Thêm file original
            String originalFilename = String.format("%s-original.%s", uuid, outputFormat);
            String originalDownloadUri = uploadPosterToFirebase(fileBytes, originalFilename, fileType);
            fileEntitylList.add(
                    buildFileEntity(
                            file,
                            originalFilename,
                            originalDownloadUri,
                            "original",
                            user.getUsername()
                    )
            );

            // Save file to database
            return fileMapper.mapToModels(fileRepository.saveAll(fileEntitylList));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    /**
     * Upload movie gallery
     *
     * @param file file
     * @return FileModel
     */
    public FileModel uploadMovieGallery(MultipartFile file) {
        validateImage(file);
        var user = authUtils.getCurrentUser();

        try {
            String fileType = tika.detect(file.getInputStream());
            byte[] fileBytes = file.getBytes();
            String ext = fileType.split("/")[1]; // jpeg, png
            String outputFormat = ext.equals("jpeg") ? "jpg" : ext;
            String uuid = UUID.randomUUID().toString();

            // resize về 1024
            int width = 1024;
            byte[] resized = resizer.resize(fileBytes, width, outputFormat);
            String filename = String.format("%s-%dw.%s", uuid, width, outputFormat);
            String downloadUri = uploadPosterToFirebase(resized, filename, fileType);

            return fileMapper.mapToModel(fileRepository.save(
                    buildFileEntity(
                            file,
                            filename,
                            downloadUri,
                            String.valueOf(width),
                            user.getUsername()
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    public FileModel uploadMovieAsset(MultipartFile file) {
        validateAsset(file);
        var user = authUtils.getCurrentUser();

        try {
            String fileType = tika.detect(file.getInputStream());
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";

            // Lấy phần mở rộng từ tên file gốc
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            } else {
                // Nếu không có phần mở rộng, sử dụng từ MIME type
                fileExtension = fileType.split("/")[1];
            }

            String uuid = UUID.randomUUID().toString();
            String date = sdf.format(new Date());
            String filename = String.format("%s-asset-%s.%s", uuid, date, fileExtension);
            String downloadUri = uploadAssetToFirebase(fileBytes, filename, fileType);

            return fileMapper.mapToModel(fileRepository.save(
                    buildFileEntity(
                            file,
                            filename,
                            downloadUri,
                            ASSET_PATH,
                            user.getUsername()
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    public FileModel uploadMovie(MultipartFile file, String type) {
        validateAsset(file);
        validateType(type);
        var user = authUtils.getCurrentUser();

        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String filePath = type + "/" + fileName;
            String presignedUrl = s3UploadService.uploadMovie(file, filePath);

            return fileMapper.mapToModel(fileRepository.save(
                    buildFileEntity(
                            file,
                            fileName,
                            presignedUrl,
                            "movies",
                            user.getUsername()
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload phim: " + e.getMessage());
        }
    }

    private void validateImage(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File không được để trống!");
        }

        // Check file size
        if (file.getSize() > MAX_IMAGE_FILE_SIZE) {
            throw new ValidationException("Kích thước file không được vượt quá 2MB");
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

    private void validateAsset(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ValidationException("File không được để trống!");
        }

        // Check file size
        if (file.getSize() > MAX_ASSET_FILE_SIZE) {
            throw new ValidationException("Kích thước file không được vượt quá 10MB");
        }
    }

    private void validateType(String type) {
        if (Strings.isBlank(type)) {
            throw new ValidationException("Type không được để trống!");
        }

        if (!MOVIE_TYPE.contains(type)) {
            throw new ValidationException("Type không hợp lệ!");
        }
    }

    private FileEntity buildFileEntity(MultipartFile file,
                                       String fileName,
                                       String downloadUri,
                                       String category,
                                       String username) {
        return FileEntity.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileDownloadUri(downloadUri)
                .fileExtension(Objects
                        .requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1))
                .fileCategory(category)
                .fileOwner(username)
                .fileUploader(username)
                .build();
    }

    private String uploadPosterToFirebase(byte[] bytes, String fileName, String contentType) {
        String path = String.format("%s/%s/%s", UPLOAD_TYPE, POSTER_PATH, fileName);
        return firebaseStorageService.uploadFile(bytes, path, contentType);
    }

    private String uploadAssetToFirebase(byte[] bytes, String fileName, String contentType) {
        String path = String.format("%s/%s/%s", UPLOAD_TYPE, ASSET_PATH, fileName);
        return firebaseStorageService.uploadFile(bytes, path, contentType);
    }

    public FileEntity getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
}
