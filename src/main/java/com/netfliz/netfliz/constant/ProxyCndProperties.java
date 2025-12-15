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
    private String url = "";

    public String getImageUrl() {
        return url + "/images";
    }

    public String getVideoUrl() {
        return url + "/videos";
    }

    public String getAssetUrl() {
        return url + "/assets";
    }

    public String getMermaidUrl() {
        return url + "/mermaid";
    }

    public String getCommonUrl() {
        return url + "/common";
    }

    public String getMarkdownUrl() {
        return url + "/markdown";
    }
}
