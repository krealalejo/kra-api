package com.kra.api.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kra.api.application.ProjectMetadataService;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import com.kra.api.infrastructure.web.dto.ProjectMetadataResponse;
import com.kra.api.infrastructure.web.dto.UpsertProjectMetadataRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectMetadataController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class ProjectMetadataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectMetadataService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMetadata_withValidParams_shouldReturnOk() throws Exception {
        ProjectMetadataResponse response = new ProjectMetadataResponse("Role", "2024", "Backend", "main", List.of("Java"));
        when(service.getMetadata("owner", "repo")).thenReturn(response);

        mockMvc.perform(get("/projects/metadata/owner/repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Role"));
    }

    @Test
    void getMetadata_withInvalidParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/projects/metadata/owner/invalid!repo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertMetadata_withValidRequest_shouldReturnOk() throws Exception {
        UpsertProjectMetadataRequest request = new UpsertProjectMetadataRequest();
        request.setRole("Lead");
        request.setYear("2025");
        request.setKind("Frontend");
        request.setMainBranch("develop");
        request.setStack(List.of("Vue"));

        ProjectMetadataResponse response = new ProjectMetadataResponse("Lead", "2025", "Frontend", "develop", List.of("Vue"));
        when(service.upsertMetadata(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList()))
                .thenReturn(response);

        mockMvc.perform(put("/projects/metadata/owner/repo")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Lead"));
    }

    @Test
    void upsertMetadata_withInvalidKind_shouldReturnBadRequest() throws Exception {
        UpsertProjectMetadataRequest request = new UpsertProjectMetadataRequest();
        request.setKind("InvalidKind");

        mockMvc.perform(put("/projects/metadata/owner/repo")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertMetadata_withoutAuth_shouldReturnUnauthorized() throws Exception {
        UpsertProjectMetadataRequest request = new UpsertProjectMetadataRequest();

        mockMvc.perform(put("/projects/metadata/owner/repo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
