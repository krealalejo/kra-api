package com.kra.api.infrastructure.web;

import com.kra.api.application.BlogPostNotFoundException;
import com.kra.api.application.BlogPostService;
import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlogPostController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class, GlobalExceptionHandler.class})
class BlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlogPostService blogPostService;

    @Test
    void listPosts_returns200() throws Exception {
        when(blogPostService.listPosts()).thenReturn(List.of());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPost_found_returns200() throws Exception {
        BlogPost post = new BlogPost(
                BlogSlug.of("hello-world"),
                "Hello",
                "Body",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"));
        when(blogPostService.getPost("hello-world")).thenReturn(post);

        mockMvc.perform(get("/posts/hello-world"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("hello-world"))
                .andExpect(jsonPath("$.title").value("Hello"));
    }

    @Test
    void getPost_missing_returns404() throws Exception {
        when(blogPostService.getPost("nope")).thenThrow(new BlogPostNotFoundException("nope"));

        mockMvc.perform(get("/posts/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void createPost_withoutJwt_returns401() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"a\",\"title\":\"T\",\"content\":\"c\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_withJwt_returns201() throws Exception {
        BlogPost created = new BlogPost(
                BlogSlug.of("new-post"),
                "Title",
                "Content",
                Instant.now(),
                Instant.now());
        when(blogPostService.createPost(eq("new-post"), eq("Title"), eq("Content"))).thenReturn(created);

        mockMvc.perform(post("/posts")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"new-post\",\"title\":\"Title\",\"content\":\"Content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("new-post"));
    }
}
