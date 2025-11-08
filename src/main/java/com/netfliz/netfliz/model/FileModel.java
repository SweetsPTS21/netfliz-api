package com.netfliz.netfliz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileModel {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileDownloadUri;
    private String fileExtension;
    private String fileDescription;
    private String fileCategory;
    private String fileTags;
    private Integer fileStatus;
    private String fileOwner;
    private String fileUploader;
    private Date createdAt;
    private Date updatedAt;
}
