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
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/portfolio/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/config/profile").permitAll()
                .requestMatchers(HttpMethod.PUT, "/config/profile").authenticated()
                .requestMatchers(HttpMethod.GET, "/activity").permitAll()
                .requestMatchers(HttpMethod.PUT, "/activity/**").authenticated()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/contact").permitAll()
                .requestMatchers(HttpMethod.POST, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/posts", "/posts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/admin/upload").authenticated()
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
