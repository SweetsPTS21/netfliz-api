package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.FileEntity;
import com.netfliz.netfliz.model.FileModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileMapper {

    public FileModel mapToModel(FileEntity entity) {
        return FileModel.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .fileExtension(entity.getFileExtension())
                .fileCategory(entity.getFileCategory())
                .fileDownloadUri(entity.getFileDownloadUri())
                .build();
    }

    public List<FileModel> mapToModels(List<FileEntity> entityList) {
        return entityList.stream().map(this::mapToModel).collect(Collectors.toList());
    }
}
