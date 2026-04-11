package com.kra.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
public record GitHubProperties(
        String token,
        String portfolioUser,
        String apiBaseUrl) {

    public GitHubProperties {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "https://api.github.com";
        }
    }
}
