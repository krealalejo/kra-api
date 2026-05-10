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

    private static final String PROJECTS_API = "/projects/**";
    private static final String POSTS_API = "/posts/**";
    private static final String CV_API = "/cv/**";

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
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, PROJECTS_API).permitAll()
                .requestMatchers(HttpMethod.GET, "/posts", POSTS_API).permitAll()
                .requestMatchers(HttpMethod.GET, "/portfolio/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/config/profile").permitAll()
                .requestMatchers(HttpMethod.PUT, "/config/profile").authenticated()
                .requestMatchers(HttpMethod.GET, "/activity").permitAll()
                .requestMatchers(HttpMethod.PUT, "/activity/**").authenticated()
                .requestMatchers(HttpMethod.GET, CV_API).permitAll()
                .requestMatchers(HttpMethod.POST, CV_API).authenticated()
                .requestMatchers(HttpMethod.PUT, CV_API).authenticated()
                .requestMatchers(HttpMethod.DELETE, CV_API).authenticated()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/contact").permitAll()
                .requestMatchers(HttpMethod.POST, PROJECTS_API).authenticated()
                .requestMatchers(HttpMethod.PUT, PROJECTS_API).authenticated()
                .requestMatchers(HttpMethod.DELETE, PROJECTS_API).authenticated()
                .requestMatchers(HttpMethod.POST, "/posts", POSTS_API).authenticated()
                .requestMatchers(HttpMethod.PUT, POSTS_API).authenticated()
                .requestMatchers(HttpMethod.DELETE, POSTS_API).authenticated()
                .requestMatchers(HttpMethod.GET, "/admin/posts").authenticated()
                .requestMatchers(HttpMethod.GET, "/admin/metrics").authenticated()
                .requestMatchers(HttpMethod.POST, "/admin/cache/flush").authenticated()
                .requestMatchers(HttpMethod.POST, "/admin/upload").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(withDefaults())
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            );

        return http.build();
    }
}
