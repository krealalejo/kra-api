package com.kra.api.infrastructure.web;

import com.kra.api.application.EducationNotFoundException;
import com.kra.api.application.EducationService;
import com.kra.api.domain.model.Education;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EducationController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
@SuppressWarnings("null")
class EducationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EducationService educationService;

    private Education sample() {
        return new Education("edu-1", "BSc CS", "MIT", "Cambridge", "2015-2019", "desc", 1);
    }

    @Test
    void list_returnsEmptyArray() throws Exception {
        when(educationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/education"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_returnsItems() throws Exception {
        when(educationService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/cv/education"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("edu-1"))
                .andExpect(jsonPath("$[0].title").value("BSc CS"))
                .andExpect(jsonPath("$[0].institution").value("MIT"))
                .andExpect(jsonPath("$[0].sortOrder").value(1));
    }

    @Test
    void list_noAuth_returns200() throws Exception {
        when(educationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/education"))
                .andExpect(status().isOk());
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(educationService.create(any(), any(), any(), any(), any(), anyInt()))
                .thenReturn(sample());

        mockMvc.perform(post("/cv/education")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"BSc CS\",\"institution\":\"MIT\",\"location\":\"Cambridge\",\"years\":\"2015-2019\",\"description\":\"desc\",\"sortOrder\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("edu-1"))
                .andExpect(jsonPath("$.title").value("BSc CS"));
    }

    @Test
    void create_blankTitle_returns400() throws Exception {
        mockMvc.perform(post("/cv/education")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"sortOrder\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void create_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/cv/education")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"BSc CS\",\"sortOrder\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_found_returns200() throws Exception {
        when(educationService.update(eq("edu-1"), any(), any(), any(), any(), any(), any()))
                .thenReturn(sample());

        mockMvc.perform(put("/cv/education/edu-1")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("edu-1"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(educationService.update(eq("missing"), any(), any(), any(), any(), any(), any()))
                .thenThrow(new EducationNotFoundException("missing"));

        mockMvc.perform(put("/cv/education/missing")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Education not found: missing"));
    }

    @Test
    void update_noAuth_returns401() throws Exception {
        mockMvc.perform(put("/cv/education/edu-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_found_returns204() throws Exception {
        mockMvc.perform(delete("/cv/education/edu-1").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new EducationNotFoundException("missing"))
                .when(educationService).delete("missing");

        mockMvc.perform(delete("/cv/education/missing").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Education not found: missing"));
    }

    @Test
    void delete_noAuth_returns401() throws Exception {
        mockMvc.perform(delete("/cv/education/edu-1"))
                .andExpect(status().isUnauthorized());
    }
}
