package com.kra.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class WebCorsConfiguration {

    private static final Logger logger = Logger.getLogger(WebCorsConfiguration.class.getName());

    /**
     * Returns the EC2_ORIGIN OS environment variable value.
     * Extracted as a protected method to allow test subclasses to inject a value
     * without requiring reflection-based env-var manipulation (which is blocked
     * by Java 21 module encapsulation).
     */
    protected String readEc2OriginEnv() {
        return System.getenv("EC2_ORIGIN");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = new ArrayList<>(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        String ec2Origin = readEc2OriginEnv();
        if (ec2Origin != null && !ec2Origin.isBlank()) {
            origins.add(ec2Origin);
        } else {
            logger.warning("EC2_ORIGIN environment variable is not set; production CORS origin is excluded from the allowlist");
        }
        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
