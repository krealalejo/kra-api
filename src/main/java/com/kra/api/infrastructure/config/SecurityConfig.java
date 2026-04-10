package com.kra.api.infrastructure.config;

import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Spring Security configuration for OAuth2 Resource Server with AWS Cognito.
 *
 * - GET /projects/**:                  public (no JWT required)
 * - POST /projects/**:                 requires valid Cognito JWT
 * - PUT /projects/**:                  requires valid Cognito JWT
 * - DELETE /projects/**:               requires valid Cognito JWT
 * - Any other request:                 requires authentication (safe default)
 *
 * JWT validation is configured via:
 *   spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt
 *
 * Error format (D-08, D-09): 401 and 403 responses use the same JSON structure as
 * GlobalExceptionHandler — {"error":"UNAUTHORIZED"/"FORBIDDEN","message":"..."}.
 *
 * Testing:
 *   Use @Import(SecurityConfig.class) in @WebMvcTest test classes.
 *   Use .with(jwt()) MockMvc post-processor for authenticated requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests((authorize) -> authorize
                // GET requests: public, no authentication required
                .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                // Actuator health: permit for load balancer / liveness probes
                .requestMatchers("/actuator/health").permitAll()
                // Write operations: require valid OAuth2 JWT
                .requestMatchers(HttpMethod.POST, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/projects/**").authenticated()
                // Any other requests: authenticated (safe default)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(withDefaults())
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            .exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            );

        return http.build();
    }
}
