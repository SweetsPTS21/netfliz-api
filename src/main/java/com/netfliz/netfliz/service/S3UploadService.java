package com.netfliz.netfliz.service;

import com.netfliz.netfliz.util.S3Properties;
import com.netfliz.netfliz.util.WorkerSignatureUtil;
import jakarta.validation.ValidationException;
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

    private static final long ALLOWED_SKEW_SECONDS = 300;

    public void uploadMovie(MultipartFile file, String filePath) {
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

    public void checkSignature(String key, String ts, String signature) {
        long timestampSec;
        try {
            timestampSec = Long.parseLong(ts);
        } catch (NumberFormatException ex) {
            throw new ValidationException("invalid timestamp");
        }
        long now = System.currentTimeMillis() / 1000L;
        if (Math.abs(now - timestampSec) > ALLOWED_SKEW_SECONDS) {
            throw new ValidationException("timestamp skew too large");
        }

        // 2) compute expected HMAC
        String message = WorkerSignatureUtil.buildMessage(key, ts);
        String expected = WorkerSignatureUtil.computeHmacHex(s3Properties.getWorkerSharedSecret(), message);

        // 3) constant-time compare
        if (!WorkerSignatureUtil.constantTimeEquals(expected, signature)) {
            throw new ValidationException("invalid signature");
        }
    }
}
