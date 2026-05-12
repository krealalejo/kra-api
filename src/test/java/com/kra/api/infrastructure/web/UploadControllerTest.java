package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.s3.S3Service;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UploadController.class)
@Import({ SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class,
        GlobalExceptionHandler.class })
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private S3Service s3Service;

    @Test
    void generateUploadUrl_withValidJwt_returns200WithUrlAndKey() throws Exception {
        when(s3Service.generateUploadUrl(any(), any(), any(), any()))
                .thenReturn(new S3Service.PresignResult(
                        "https://kra-assets.s3.eu-west-1.amazonaws.com/uploads/blog/my-post.jpg?X-Amz-Signature=...",
                        "blog/my-post-cover.webp"));

        mockMvc.perform(post("/admin/upload")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filename\":\"photo.jpg\",\"contentType\":\"image/jpeg\",\"entitySlug\":\"my-post\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").isString())
                .andExpect(jsonPath("$.s3Key").value("blog/my-post-cover.webp"));
    }

    @Test
    void generateUploadUrl_withoutJwt_returns401() throws Exception {
        mockMvc.perform(post("/admin/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filename\":\"photo.jpg\",\"contentType\":\"image/jpeg\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void generateUploadUrl_withInvalidContentType_returns400() throws Exception {
        mockMvc.perform(post("/admin/upload")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filename\":\"doc.txt\",\"contentType\":\"text/plain\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void generateUploadUrl_withPdfContentType_returns200() throws Exception {
        when(s3Service.generateUploadUrl(any(), any(), any(), any()))
                .thenReturn(new S3Service.PresignResult(
                        "https://kra-assets.s3.eu-west-1.amazonaws.com/documents/cv.pdf?X-Amz-Signature=...",
                        "documents/cv.pdf"));

        mockMvc.perform(post("/admin/upload")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filename\":\"cv.pdf\",\"contentType\":\"application/pdf\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.s3Key").value("documents/cv.pdf"));
    }

    @Test
    void generateUploadUrl_withMissingFilename_returns400() throws Exception {
        mockMvc.perform(post("/admin/upload")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentType\":\"image/jpeg\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void deleteS3Object_withValidJwt_returns204() throws Exception {
        mockMvc.perform(delete("/admin/s3")
                        .with(jwt())
                        .param("key", "blog/my-post-cover.webp"))
                .andExpect(status().isNoContent());

        verify(s3Service).deleteObject("blog/my-post-cover.webp");
    }

    @Test
    void deleteS3Object_withoutJwt_returns401() throws Exception {
        mockMvc.perform(delete("/admin/s3")
                        .param("key", "blog/my-post-cover.webp"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteS3Object_withPathTraversal_returns400() throws Exception {
        mockMvc.perform(delete("/admin/s3")
                        .with(jwt())
                        .param("key", "../secrets/key"))
                .andExpect(status().isBadRequest());
    }
}
