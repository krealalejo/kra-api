package com.kra.api.infrastructure.web;

import com.kra.api.application.SkillCategoryNotFoundException;
import com.kra.api.application.SkillCategoryService;
import com.kra.api.domain.model.SkillCategory;
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

@WebMvcTest(SkillCategoryController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class SkillCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SkillCategoryService skillCategoryService;

    private SkillCategory sample() {
        return new SkillCategory("cat-1", "Backend", List.of("Java", "Spring"), 1);
    }

    @Test
    void list_returnsEmptyArray() throws Exception {
        when(skillCategoryService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/skills/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_returnsItems() throws Exception {
        when(skillCategoryService.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/cv/skills/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cat-1"))
                .andExpect(jsonPath("$[0].name").value("Backend"))
                .andExpect(jsonPath("$[0].skills[0]").value("Java"))
                .andExpect(jsonPath("$[0].sortOrder").value(1));
    }

    @Test
    void list_noAuth_returns200() throws Exception {
        when(skillCategoryService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/cv/skills/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(skillCategoryService.create(any(), any(), anyInt()))
                .thenReturn(sample());

        mockMvc.perform(post("/cv/skills/categories")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Backend\",\"skills\":[\"Java\",\"Spring\"],\"sortOrder\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cat-1"))
                .andExpect(jsonPath("$.name").value("Backend"));
    }

    @Test
    void create_blankName_returns400() throws Exception {
        mockMvc.perform(post("/cv/skills/categories")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"sortOrder\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void create_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/cv/skills/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Backend\",\"sortOrder\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_found_returns200() throws Exception {
        when(skillCategoryService.update(eq("cat-1"), any(), any(), any()))
                .thenReturn(sample());

        mockMvc.perform(put("/cv/skills/categories/cat-1")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Backend & APIs\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cat-1"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(skillCategoryService.update(eq("missing"), any(), any(), any()))
                .thenThrow(new SkillCategoryNotFoundException("missing"));

        mockMvc.perform(put("/cv/skills/categories/missing")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"T\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Skill category not found: missing"));
    }

    @Test
    void update_noAuth_returns401() throws Exception {
        mockMvc.perform(put("/cv/skills/categories/cat-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"T\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_found_returns204() throws Exception {
        mockMvc.perform(delete("/cv/skills/categories/cat-1").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new SkillCategoryNotFoundException("missing"))
                .when(skillCategoryService).delete("missing");

        mockMvc.perform(delete("/cv/skills/categories/missing").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Skill category not found: missing"));
    }

    @Test
    void delete_noAuth_returns401() throws Exception {
        mockMvc.perform(delete("/cv/skills/categories/cat-1"))
                .andExpect(status().isUnauthorized());
    }
}
