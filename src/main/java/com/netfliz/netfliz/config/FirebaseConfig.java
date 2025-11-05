package com.netfliz.netfliz.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.config.base64}")
    private String firebaseConfigBase64;

    @PostConstruct
    public void init() throws IOException {
        InputStream inputStream;

        if (firebaseConfigPath == null || !new File(firebaseConfigPath).exists()) {
            if (firebaseConfigBase64 == null) {
                throw new FileNotFoundException("❌ Firebase config file or base64 not found.");
            }

            byte[] decoded = Base64.getDecoder().decode(firebaseConfigBase64);
            inputStream = new ByteArrayInputStream(decoded);
        } else {
            inputStream = new FileInputStream(firebaseConfigPath);
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .setStorageBucket("netfliz-19a9c.appspot.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
