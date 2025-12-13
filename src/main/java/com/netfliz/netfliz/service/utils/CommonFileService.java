package com.netfliz.netfliz.service.utils;

import com.netfliz.netfliz.constant.ProxyCndProperties;
import com.netfliz.netfliz.constant.UploadKey;
import com.netfliz.netfliz.mapper.FileMapper;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.repository.IFileRepository;
import com.netfliz.netfliz.service.FileService;
import com.netfliz.netfliz.service.FirebaseStorageService;
import com.netfliz.netfliz.validator.FileValidator;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommonFileService {
    private final FileService fileService;
    private final FileMapper fileMapper;
    private final FileValidator fileValidator;
    private final IFileRepository fileRepository;
    private final ProxyCndProperties proxyCndProperties;
    private final FirebaseStorageService firebaseStorageService;
    private final Tika tika = new Tika();

    public FileModel uploadFile(MultipartFile file) {
        fileValidator.validateCommonFile(file);

        try {
            String fileType = tika.detect(file.getInputStream());
            String fileExtension = Optional.ofNullable(file.getOriginalFilename())
                    .map((extension) -> extension.split("\\.")[1])
                    .orElse(fileType.split("/")[1]);
            String uuid = UUID.randomUUID().toString();
            String filename = String.format("%s-%s.%s", uuid, "file", fileExtension);

            // upload file
            firebaseStorageService.uploadFile(
                    file.getBytes(),
                    String.format("%s/%s/%s", UploadKey.COMMON_PATH, "file", filename),
                    fileType);

            String fileDownloadUri = String.format("%s/%s/%s", proxyCndProperties.getCommonUrl(), "file", filename);

            return fileMapper.mapToModel(fileRepository.save(
                    fileService.buildFileEntity(
                            file,
                            filename,
                            fileDownloadUri,
                            UploadKey.COMMON_PATH,
                            "anonymous"
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi đọc file file: " + e.getMessage());
        }
    }
}
