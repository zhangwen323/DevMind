package com.devmind.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devmind")
public record BackendProperties(String apiBasePath) {
}
