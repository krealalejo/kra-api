package com.kra.api;

import com.kra.api.infrastructure.config.GitHubProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GitHubProperties.class)
public class KraApiApplication {

    public static void main(String[] args) {
        loadLocalDotEnv();
        SpringApplication.run(KraApiApplication.class, args);
    }

    private static void loadLocalDotEnv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                return;
            }
            if (System.getenv(key) != null) {
                return;
            }
            if (System.getProperty(key) != null) {
                return;
            }
            System.setProperty(key, entry.getValue());
        });
    }
}
