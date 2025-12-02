package com.netfliz.netfliz.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.netfliz.netfliz.constant.FirebaseProperties;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Base64;

@Configuration
@AllArgsConstructor
public class FirebaseConfig {
    private final FirebaseProperties firebaseProperties;

    @PostConstruct
    public void init() throws IOException {
        InputStream inputStream;
        String path = firebaseProperties.getConfigPath();
        String base64 = firebaseProperties.getConfigBase64();

        if (!new File(path).exists()) {
            if (Strings.isBlank(base64)) {
                throw new FileNotFoundException("❌ Firebase config file or base64 not found.");
            }

            byte[] decoded = Base64.getDecoder().decode(base64);
            inputStream = new ByteArrayInputStream(decoded);
        } else {
            inputStream = new FileInputStream(path);
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .setStorageBucket(firebaseProperties.getBucketName())
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
