package com.netfliz.netfliz.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "proxy.cdn")
public class ProxyCndProperties {
    private String imageUrl = "";
    private String videoUrl = "";
    private String assetUrl = "";
    private String mermaidUrl = "";
    private String commonUrl = "";
}
