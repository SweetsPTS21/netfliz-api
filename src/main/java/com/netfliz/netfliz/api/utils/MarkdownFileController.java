package com.netfliz.netfliz.api.utils;

import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.utils.MarkdownService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/utils/markdown-file")
public class MarkdownFileController {
    private final MarkdownService markdownService;

    @GetMapping("/{name}")
    public ResponseEntity<FileModel> getMarkdownFile(@PathVariable String name) {
        return ResponseEntity.ok(markdownService.getMarkdownFile(name));
    }

    @PostMapping
    public ResponseEntity<FileModel> createMarkdownFile(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(markdownService.createMarkdownFile(file));
    }
}
