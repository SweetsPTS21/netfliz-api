package com.netfliz.netfliz.service;

import com.netfliz.netfliz.entity.FileEntity;
import com.netfliz.netfliz.mapper.FileMapper;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.repository.IFileRepository;
import com.netfliz.netfliz.util.AuthUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private final AuthUtils authUtils;
    private final ImageResizerService resizer;
    private final Tika tika = new Tika();

    private static final String UPLOAD_TYPE = "movies";
    private static final String PATH_TYPE = "poster";
    private final int[] TARGET_WIDTHS = new int[]{320, 640, 1024};

    /**
     * Upload file
     *
     * @param file file
     * @return FileModel
     */
    public List<FileModel> uploadFile(MultipartFile file) {
        if (Objects.isNull(file)) {
            throw new ValidationException("Lỗi khi upload file!");
        }

        try {
            var user = authUtils.getCurrentUser();
            String fileType = tika.detect(file.getInputStream());
            if (!fileType.startsWith("image/")) {
                throw new ValidationException("File không phải ảnh.");
            }

            String ext = fileType.split("/")[1]; // jpeg, png, webp...
            String outputFormat = ext.equals("jpeg") ? "jpg" : ext;
            String uuid = UUID.randomUUID().toString();
            List<FileModel> fileModelList = new ArrayList<>();

            for (int width : TARGET_WIDTHS) {
                byte[] resized = resizer.resize(file, width, outputFormat);
                String filename = String.format("%s-%dw.%s", uuid, width, outputFormat);

                String downloadUri = uploadPosterToFirebase(resized, filename, fileType);
                fileModelList.add(
                        buildFileModel(
                                file,
                                downloadUri,
                                String.valueOf(width),
                                user.getUsername()
                        )
                );
            }

            // Thêm file original
            String originalFilename = String.format("%s-original.%s", uuid, outputFormat);
            String originalDownloadUri = uploadPosterToFirebase(file.getBytes(), originalFilename, fileType);
            fileModelList.add(
                    buildFileModel(
                            file,
                            originalDownloadUri,
                            "original",
                            user.getUsername()
                    )
            );

            // Lưu tất cả file
            var fileEntities = fileRepository.saveAll(fileMapper.mapToEntities(fileModelList));

            // Save file to database
            return fileMapper.mapToModels(fileEntities);
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi upload file!");
        }
    }

    private FileModel buildFileModel(MultipartFile file, String downloadUri, String category, String username) {
        return FileModel.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileDownloadUri(downloadUri)
                .fileExtension(Objects
                        .requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1))
                .fileOwner(username)
                .fileUploader(username)
                .build();
    }

    private String uploadPosterToFirebase(byte[] bytes, String fileName, String contentType) {
        String path = String.format("%s/%s/%s", UPLOAD_TYPE, PATH_TYPE, fileName);
        return firebaseStorageService.uploadFile(bytes, path, contentType);
    }

    public FileEntity getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
}
