package com.netfliz.netfliz.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.netfliz.netfliz.exception.StorageException;
import com.netfliz.netfliz.util.FirebaseProperties;
import com.netfliz.netfliz.util.ProxyCndProperties;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class FirebaseStorageService {
    private final FirebaseProperties firebaseProperties;
    private final ProxyCndProperties proxyCndProperties;

    public String uploadPoster(byte[] bytes, String path, String contentType) {
        uploadFile(bytes, path, contentType);
        return proxyCndProperties.getImageUrl() + path;
    }

    public String uploadAsset(byte[] bytes, String path, String contentType) {
        uploadFile(bytes, path, contentType);
        return proxyCndProperties.getAssetUrl() + path;
    }

    /**
     * Upload file to Firebase Storage
     *
     * @param bytes       data
     * @param path        path
     * @param contentType contentType
     */
    private void uploadFile(byte[] bytes, String path, String contentType) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(path, bytes, contentType);
        blob = blob.toBuilder()
                .setCacheControl("public, max-age=31536000, s-maxage=31536000, immutable") // max-age 1 year
                .build().update();

        // make public (no token needed)
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    }

    /**
     * Constructs a public download URL with token for the given Firebase Storage file URL
     * Include token in url
     *
     * @param filePath the path of the file in Firebase Storage
     * @return the public download URL with access token
     * @throws IllegalArgumentException if fileUrl is null or empty
     */
    public String getDownloadUrl(String filePath) {
        if (Strings.isBlank(filePath)) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        try {
            Bucket bucket = StorageClient.getInstance().bucket();

            // Extract the path from the full URL
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1); // Remove leading slash
            }

            // Get the blob metadata to retrieve the download token
            Blob blob = bucket.get(filePath);
            if (blob == null) {
                throw new StorageException("File not found in storage");
            }

            // Get the download token
            String token = blob.getBlobId().getGeneration().toString();

            // Construct the public URL with token
            return String.format(
                    "%s%s?alt=media&token=%s",
                    firebaseProperties.getBaseUrl(),
                    URLEncoder.encode(filePath, StandardCharsets.UTF_8).replace("+", "%20"),
                    token
            );

        } catch (Exception e) {
            throw new StorageException("Error generating download URL: " + e.getMessage(), e);
        }
    }
}

