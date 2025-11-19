package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload-poster")
    public ResponseEntity<List<FileModel>> uploadMoviePoster(MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadMoviePoster(file));
    }
}
