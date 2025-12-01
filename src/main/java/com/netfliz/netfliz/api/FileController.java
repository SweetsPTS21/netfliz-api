package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.FileModel;
import com.netfliz.netfliz.service.FileService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload-poster")
    public ResponseEntity<List<FileModel>> uploadMoviePoster(MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadMoviePoster(file));
    }

    @PostMapping("/upload-gallery")
    public ResponseEntity<FileModel> uploadMovieGallery(MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadMovieGallery(file));
    }

    @PostMapping("/upload-asset")
    public ResponseEntity<FileModel> uploadMovieAsset(MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadMovieAsset(file));
    }

    @PostMapping("/upload-movie")
    public ResponseEntity<FileModel> uploadMovie(MultipartFile file, String type) {
        return ResponseEntity.ok(fileService.uploadMovie(file, type));
    }

    @GetMapping("/presign-url")
    @Schema(description = "Trả về Presign URL để truy cập file từ BackBlaze B2")
    public ResponseEntity<Object> presignUrl(@RequestParam("key") String key,
                                             @RequestHeader(value = "X-Worker-Timestamp", required = false) String ts,
                                             @RequestHeader(value = "X-Worker-Signature", required = false) String signature) {

        if (Strings.isBlank(key)) {
            return ResponseEntity.badRequest().body("missing key");
        }
        if (Strings.isBlank(ts) || Strings.isBlank(signature)) {
            return ResponseEntity.status(401).body("missing authentication headers");
        }

        return ResponseEntity.ok(fileService.presignUrl(key, ts, signature));
    }
}
