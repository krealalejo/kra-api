package com.kra.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Objects;

@Configuration
public class GitHubWebClientConfiguration {

    @Bean(name = "githubWebClient")
    public WebClient githubWebClient(GitHubProperties properties) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(Objects.requireNonNull(properties.apiBaseUrl()))
                .defaultHeader(HttpHeaders.USER_AGENT, "kra-api-portfolio");
        if (properties.token() != null && !properties.token().isBlank()) {
            builder = builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token());
        }
        return builder.build();
    }
}
