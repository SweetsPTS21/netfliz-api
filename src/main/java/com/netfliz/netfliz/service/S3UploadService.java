package com.netfliz.netfliz.service;

import com.netfliz.netfliz.util.S3Properties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@AllArgsConstructor
public class S3UploadService {
    private final S3Client s3;
    private final S3Presigner presigner;
    private final S3Properties s3Properties;

    public String uploadMovie(MultipartFile file, String filePath) {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(filePath)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .build();

        try {
            s3.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


        return generatePresignedUrl(filePath, Duration.ofHours(1));
    }

    public String generatePresignedUrl(String key, Duration validFor) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(validFor)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
