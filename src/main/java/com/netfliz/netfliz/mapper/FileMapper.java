package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.FileEntity;
import com.netfliz.netfliz.model.FileModel;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileModel mapToModel(FileEntity entity) {
        return FileModel.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .fileCategory(entity.getFileCategory())
                .fileDescription(entity.getFileDescription())
                .fileDownloadUri(entity.getFileDownloadUri())
                .fileExtension(entity.getFileExtension())
                .fileStatus(entity.getFileStatus())
                .fileOwner(entity.getFileOwner())
                .fileUploader(entity.getFileUploader())
                .fileTags(entity.getFileTags())
                .build();
    }

    public FileEntity mapToEntity(FileModel model) {
        return FileEntity.builder()
                .id(model.getId())
                .fileName(model.getFileName())
                .fileType(model.getFileType())
                .fileSize(model.getFileSize())
                .fileCategory(model.getFileCategory())
                .fileDescription(model.getFileDescription())
                .fileDownloadUri(model.getFileDownloadUri())
                .fileExtension(model.getFileExtension())
                .fileStatus(model.getFileStatus())
                .fileOwner(model.getFileOwner())
                .fileUploader(model.getFileUploader())
                .fileTags(model.getFileTags())
                .build();
    }
}
