package com.netfliz.netfliz.api.utils;

import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.utils.CommonFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/utils/upload-file")
public class CommonFileController {
    private final CommonFileService commonFileService;

    @PostMapping
    public ResponseEntity<FileModel> uploadFile(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(commonFileService.uploadFile(file));
    }
}
