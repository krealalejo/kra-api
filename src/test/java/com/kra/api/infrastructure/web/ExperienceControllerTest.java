package com.kra.api.infrastructure.web;

import com.kra.api.application.ExperienceNotFoundException;
import com.kra.api.application.ExperienceService;
import com.kra.api.domain.model.Experience;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperienceController.class)
class ExperienceControllerTest extends AbstractControllerTest {

    @MockitoBean
    private ExperienceService experienceService;

    private Experience sample() {
        return new Experience("exp-1", "Engineer", "Acme", "London", "2020-2023", "desc", 1);
    }

    @Test
    void list_returnsEmptyArray() throws Exception {
        when(experienceService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/experience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_returnsItems() throws Exception {
        when(experienceService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/cv/experience"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("exp-1"))
                .andExpect(jsonPath("$[0].title").value("Engineer"))
                .andExpect(jsonPath("$[0].company").value("Acme"))
                .andExpect(jsonPath("$[0].sortOrder").value(1));
    }

    @Test
    void list_noAuth_returns200() throws Exception {
        when(experienceService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/experience"))
                .andExpect(status().isOk());
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(experienceService.create(any(), any(), any(), any(), any(), anyInt()))
                .thenReturn(sample());

        mockMvc.perform(post("/cv/experience")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Engineer\",\"company\":\"Acme\",\"location\":\"London\",\"years\":\"2020-2023\",\"description\":\"desc\",\"sortOrder\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("exp-1"))
                .andExpect(jsonPath("$.title").value("Engineer"));
    }

    @Test
    void create_blankTitle_returns400() throws Exception {
        mockMvc.perform(post("/cv/experience")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"sortOrder\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void create_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/cv/experience")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Engineer\",\"sortOrder\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_found_returns200() throws Exception {
        when(experienceService.update(eq("exp-1"), any(), any(), any(), any(), any(), any()))
                .thenReturn(sample());

        mockMvc.perform(put("/cv/experience/exp-1")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("exp-1"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(experienceService.update(eq("missing"), any(), any(), any(), any(), any(), any()))
                .thenThrow(new ExperienceNotFoundException("missing"));

        mockMvc.perform(put("/cv/experience/missing")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Experience not found: missing"));
    }

    @Test
    void update_noAuth_returns401() throws Exception {
        mockMvc.perform(put("/cv/experience/exp-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_found_returns204() throws Exception {
        mockMvc.perform(delete("/cv/experience/exp-1").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new ExperienceNotFoundException("missing"))
                .when(experienceService).delete("missing");

        mockMvc.perform(delete("/cv/experience/missing").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Experience not found: missing"));
    }

    @Test
    void delete_noAuth_returns401() throws Exception {
        mockMvc.perform(delete("/cv/experience/exp-1"))
                .andExpect(status().isUnauthorized());
    }
}
