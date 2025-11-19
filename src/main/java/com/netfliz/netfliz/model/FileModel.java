package com.netfliz.netfliz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileModel {
    private Long id;
    private String fileName;
    private String fileDownloadUri;
}
