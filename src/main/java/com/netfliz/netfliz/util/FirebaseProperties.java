package com.netfliz.netfliz.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "firebase.storage")
public class FirebaseProperties {
    private String baseUrl = "https://firebasestorage.googleapis.com/v0/b/";
    private String configPath = "/data/firebase-service-account.json";
    private String configBase64 = "";
}
