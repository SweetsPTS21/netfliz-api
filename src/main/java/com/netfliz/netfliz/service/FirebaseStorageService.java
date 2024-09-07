package com.netfliz.netfliz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseStorageService {
    private final Logger logger = LoggerFactory.getLogger(FirebaseStorageService.class);

    @Value("${firebase.storage.base-url}")
    private String baseUrl;

    public String uploadFile(MultipartFile file, String type, String objectId) {
        String pathId = Objects.isNull(objectId) ? file.getOriginalFilename() : objectId;

        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String filePath = type + "/" + pathId + "/" + fileName;

            bucket.create(filePath, file.getBytes(), file.getContentType());
            String fileUrl = baseUrl + type + "%2F" + pathId + "%2F" + fileName;

            return getDownloadUrl(fileUrl);
        } catch (IOException e) {
            logger.error("Failed to upload file to Firebase Storage");
        }

        return "";
    }

    public String getDownloadUrl(String fileUrl) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .timeout(java.time.Duration.ofMinutes(1))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);

            return fileUrl + "?alt=media&token=" + jsonNode.get("downloadTokens").asText();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

