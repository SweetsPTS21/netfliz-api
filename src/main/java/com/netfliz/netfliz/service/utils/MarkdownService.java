package com.netfliz.netfliz.service.utils;

import com.netfliz.netfliz.constant.ProxyCndProperties;
import com.netfliz.netfliz.constant.UploadKey;
import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class MarkdownService {
    private final ProxyCndProperties proxyCndProperties;
    private final FileService fileService;

    public FileModel getMarkdownFile(String name) {
        return fileService.findFilesByName(name, UploadKey.MARKDOWN_EXT);
    }

    public FileModel createMarkdownFile(MultipartFile file) {
        return fileService.uploadFile(
                file,
                UploadKey.MARKDOWN_EXT,
                UploadKey.MARKDOWN_PATH,
                proxyCndProperties.getMarkdownUrl());
    }
}
