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
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MermaidService {
    private final FileValidator fileValidator;
    private final FirebaseStorageService firebaseStorageService;
    private final ProxyCndProperties proxyCndProperties;
    private final IFileRepository fileRepository;
    private final FileMapper fileMapper;
    private final FileService fileService;

    public FileModel getMermaidFile(String name) {
        if (Strings.isBlank(name)) {
            throw new ValidationException("Không tìm thấy file");
        }
        var files = fileRepository.findByFileName(String.format("%s.mmd", name));
        if (files.isEmpty()) {
            throw new ValidationException("Không tìm thấy file");
        }

        return fileMapper.mapToModel(files.get(0));
    }

    public FileModel createMermaidFile(MultipartFile file) {
        fileValidator.validateMermaidFile(file);

        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            String fileExtension = "mmd";
            String uuid = UUID.randomUUID().toString();

            String filename = String.format("%s-%s.%s", uuid, "mermaid", fileExtension);
            String path = String.format("%s/%s/%s", UploadKey.MERMAID_PATH, "file", filename);
            firebaseStorageService.uploadFile(fileBytes, path, "text/plain; charset=utf-8");

            String fileDownloadUri = String.format("%s/%s/%s", proxyCndProperties.getMermaidUrl(), "file", filename);

            return fileMapper.mapToModel(fileRepository.save(
                    fileService.buildFileEntity(
                            file,
                            filename,
                            fileDownloadUri,
                            UploadKey.MERMAID_PATH,
                            "anonymous"
                    )
            ));
        } catch (Exception e) {
            throw new ValidationException("Lỗi khi đọc file file: " + e.getMessage());
        }
    }
}
