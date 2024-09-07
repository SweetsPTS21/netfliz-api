package com.netfliz.netfliz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nf_file")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileDownloadUri;
    private String fileExtension;
    private String fileDescription;
    private String fileCategory;
    private String fileTags;
    private String fileStatus;
    private String fileOwner;
    private String fileUploader;
    private Date createdDate;
    private Date updatedDate;
}
