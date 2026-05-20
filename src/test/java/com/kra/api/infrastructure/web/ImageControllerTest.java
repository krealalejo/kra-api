package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.s3.S3Service;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@Import({ SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class,
        GlobalExceptionHandler.class })
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private S3Service s3Service;

    @Test
    void streamImage_existingKey_returns200WithBytes() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3};
        GetObjectResponse response = GetObjectResponse.builder().contentType("image/webp").build();
        ResponseInputStream<GetObjectResponse> stream =
                new ResponseInputStream<>(response, AbortableInputStream.create(new ByteArrayInputStream(imageBytes)));

        when(s3Service.streamObject("thumbnails/photo-thumb.webp")).thenReturn(stream);

        mockMvc.perform(get("/images/thumbnails/photo-thumb.webp"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/webp"))
                .andExpect(header().string("Cache-Control", "public, max-age=604800"));
    }

    @Test
    void streamImage_missingKey_returns404() throws Exception {
        when(s3Service.streamObject("thumbnails/missing.webp"))
                .thenThrow(NoSuchKeyException.builder().message("not found").build());

        mockMvc.perform(get("/images/thumbnails/missing.webp"))
                .andExpect(status().isNotFound());
    }

    @Test
    void streamImage_pathTraversal_returns400() throws Exception {
        mockMvc.perform(get("/images/../../etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void streamImage_noAuth_returns200() throws Exception {
        byte[] imageBytes = new byte[]{1};
        GetObjectResponse response = GetObjectResponse.builder().contentType("image/jpeg").build();
        ResponseInputStream<GetObjectResponse> stream =
                new ResponseInputStream<>(response, AbortableInputStream.create(new ByteArrayInputStream(imageBytes)));

        when(s3Service.streamObject("images/photo.jpg")).thenReturn(stream);

        mockMvc.perform(get("/images/images/photo.jpg"))
                .andExpect(status().isOk());
    }
}
