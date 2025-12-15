package com.netfliz.netfliz.service;

import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.constant.ProxyCndProperties;
import com.netfliz.netfliz.constant.UploadKey;
import com.netfliz.netfliz.entity.FileEntity;
import com.netfliz.netfliz.mapper.FileMapper;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.model.response.PresignUrlResponse;
import com.netfliz.netfliz.repository.IFileRepository;
import com.netfliz.netfliz.util.AuthUtils;
import com.netfliz.netfliz.validator.FileValidator;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
    private final ProxyCndProperties proxyCndProperties;
    private final FileValidator fileValidator;
    private final RedisService redisService;

    private final Tika tika = new Tika();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Upload movie poster
     *
     * @param file file (jpg, png)
     * @return List<FileModel>
     */
    public List<FileModel> uploadMoviePoster(MultipartFile file) {
        fileValidator.validateImage(file);
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
            for (int width : UploadKey.TARGET_WIDTHS) {
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
        fileValidator.validateImage(file);
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

    /**
     * Upload movie assets (trailer, sub,...) to firebase
     *
     * @param file file
     * @return FileModel
     */
    public FileModel uploadMovieAsset(MultipartFile file) {
        fileValidator.validateAsset(file);
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
                            UploadKey.ASSET_PATH,
                            user.getUsername()
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    /**
     * Upload movie to backblaze b2 storage
     *
     * @param file file
     * @param type (trailers/movies)
     * @return FileModel
     */
    public FileModel uploadMovie(MultipartFile file, String type) {
        fileValidator.validateVideo(file);
        fileValidator.validateType(type);
        var user = authUtils.getCurrentUser();

        try {
            String fileType = tika.detect(file.getInputStream());
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";

            // Lấy phần mở rộng từ tên file gốc
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            } else {
                // Nếu không có phần mở rộng, sử dụng từ MIME type
                fileExtension = fileType.split("/")[1];
            }

            String fileName = String.format("%s-%s.%s", UUID.randomUUID(), UploadKey.MOVIE_PATH, fileExtension);
            String filePath = type + "/" + fileName;

            // upload video to backblaze b2 storage
            s3UploadService.uploadMovie(file, filePath);
            String downloadUri = String.format("%s/%s", proxyCndProperties.getVideoUrl(), filePath);

            return fileMapper.mapToModel(fileRepository.save(
                    buildFileEntity(
                            file,
                            fileName,
                            downloadUri,
                            UploadKey.MOVIE_PATH,
                            user.getUsername()
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload phim: " + e.getMessage());
        }
    }

    /**
     * Get backblaze presigned URL
     *
     * @param key       path to object
     * @param ts        timestamp
     * @param signature signature
     * @return PresignUrlResponse
     */
    public PresignUrlResponse presignUrl(String key, String ts, String signature) {
        s3UploadService.checkSignature(key, ts, signature);
        Integer time = UploadKey.PRESIGN_URL_TIME;
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_PRESIGN_URL, key);
        var cacheData = redisService.get(cacheKey, PresignUrlResponse.class);
        if (Objects.nonNull(cacheData)) {
            return cacheData;
        }

        String url = s3UploadService.generatePresignedUrl(key, Duration.ofSeconds(time));
        PresignUrlResponse response = PresignUrlResponse.builder()
                .url(url)
                .expires(time)
                .build();

        redisService.set(cacheKey, response); // cache 30m
        return response;
    }

    /**
     * Upload file to firebase storage
     *
     * @param file file
     * @param ext  extension
     * @param type file type
     * @param cdn  cdn url
     */
    public FileModel uploadFile(MultipartFile file, String ext, String type, String cdn) {
        fileValidator.validateFile(file, ext);

        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            String fileExtension = "mmd";
            String uuid = UUID.randomUUID().toString();

            String filename = String.format("%s-%s.%s", uuid, "mermaid", fileExtension);
            String path = String.format("%s/%s/%s", type, "file", filename);
            firebaseStorageService.uploadFile(fileBytes, path, "text/plain; charset=utf-8");

            String fileDownloadUri = String.format("%s/%s/%s", cdn, "file", filename);

            return fileMapper.mapToModel(fileRepository.save(
                    buildFileEntity(
                            file,
                            filename,
                            fileDownloadUri,
                            type,
                            "anonymous"
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi đọc file file: " + e.getMessage());
        }
    }

    public FileModel findFilesByName(String name, String ext) {
        if (Strings.isBlank(name)) {
            throw new ValidationException("Không tìm thấy file");
        }
        var files = fileRepository.findByFileName(String.format("%s.%s", name, ext));
        if (files.isEmpty()) {
            throw new ValidationException("Không tìm thấy file");
        }

        return fileMapper.mapToModel(files.get(0));
    }


    public FileEntity buildFileEntity(MultipartFile file,
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
        String path = String.format("%s/%s/%s", UploadKey.MOVIE_PATH, UploadKey.POSTER_PATH, fileName);
        return firebaseStorageService.uploadPoster(bytes, path, contentType);
    }

    private String uploadAssetToFirebase(byte[] bytes, String fileName, String contentType) {
        String path = String.format("%s/%s/%s", UploadKey.MOVIE_PATH, UploadKey.ASSET_PATH, fileName);
        return firebaseStorageService.uploadAsset(bytes, path, contentType);
    }

    public FileEntity getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
}
