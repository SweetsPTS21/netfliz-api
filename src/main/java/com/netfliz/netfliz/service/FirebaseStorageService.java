package com.netfliz.netfliz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.netfliz.netfliz.util.FirebaseProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@AllArgsConstructor
public class FirebaseStorageService {
    private final FirebaseProperties firebaseProperties;

    /**
     * Upload file to Firebase Storage
     *
     * @param bytes       data
     * @param path        path
     * @param contentType contentType
     * @return public uri
     */
    public String uploadFile(byte[] bytes, String path, String contentType) {
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(path, bytes, contentType);
        String fileUrl = firebaseProperties.getBaseUrl() + path;

        return getDownloadUrl(fileUrl);
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

