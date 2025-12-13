package com.netfliz.netfliz.api.utils;

import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.utils.MermaidService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/utils/mermaid-file")
public class MermaidFileController {
    private final MermaidService mermaidService;

    @GetMapping("/{name}")
    public ResponseEntity<FileModel> getMermaidFile(@PathVariable String name) {
        return ResponseEntity.ok(mermaidService.getMermaidFile(name));
    }

    @PostMapping
    public ResponseEntity<FileModel> createMermaidFile(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(mermaidService.createMermaidFile(file));
    }
}
