package com.netfliz.netfliz.service;

import com.netfliz.netfliz.mapper.FileMapper;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.repository.IFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**.
 * File service
 */
@Service
public class FileService {
    private final IFileRepository fileRepository;
    private final FileMapper fileMapper;
    private final FirebaseStorageService firebaseStorageService;

    private static final String UPLOAD_TYPE = "movies";

    public FileService(IFileRepository fileRepository, FileMapper fileMapper, FirebaseStorageService firebaseStorageService) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.firebaseStorageService = firebaseStorageService;
    }

    /**
     * Upload file
     * @param file file
     * @return FileModel
     */
    public FileModel uploadFile(MultipartFile file, String objectId) {
        FileModel fileModel = FileModel.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileDownloadUri(getFirebaseDownloadUri(file, objectId))
                .fileExtension(Objects
                        .requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1))
                .build();

        // Save file to database
        return fileMapper.mapToModel(fileRepository.save(fileMapper.mapToEntity(fileModel)));
    }

    /**
     * Get firebase download uri
     * @param file file
     * @return String
     */
    public String getFirebaseDownloadUri(MultipartFile file, String objectId) {
        // Upload file to firebase
        return firebaseStorageService.uploadFile(file, UPLOAD_TYPE, objectId);
    }
}
