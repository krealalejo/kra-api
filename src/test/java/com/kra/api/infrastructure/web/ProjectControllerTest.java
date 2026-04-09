package com.kra.api.infrastructure.web;

import com.kra.api.application.ProjectNotFoundException;
import com.kra.api.application.ProjectService;
import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    // --- POST /projects ---

    @Test
    void createProject_validRequest_returns201() throws Exception {
        Project fakeProject = new Project(ProjectId.of("abc-123"),
                "My Project", "desc", "https://url.com", "content");
        when(projectService.createProject(any(), any(), any(), any())).thenReturn(fakeProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.title").value("My Project"));
    }

    @Test
    void createProject_blankTitle_returns400() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"description\":\"desc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void createProject_missingTitle_returns400() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"only desc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    // --- GET /projects ---

    @Test
    void listProjects_returns200AndArray() throws Exception {
        when(projectService.getAllProjects(50)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listProjects_withLimit_returnsCapped() throws Exception {
        Project p1 = new Project(ProjectId.of("1"), "T1", null, null, null);
        Project p2 = new Project(ProjectId.of("2"), "T2", null, null, null);
        when(projectService.getAllProjects(2)).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/projects").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // --- GET /projects/{id} ---

    @Test
    void getById_found_returns200() throws Exception {
        Project project = new Project(ProjectId.of("abc-123"), "My Project", "desc", "https://url.com", "content");
        when(projectService.getProjectById("abc-123")).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.title").value("My Project"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(projectService.getProjectById("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Project not found: missing"));
    }

    // --- PUT /projects/{id} ---

    @Test
    void updateProject_found_returns200() throws Exception {
        Project updated = new Project(ProjectId.of("abc-123"), "Updated", "new desc", null, null);
        when(projectService.updateProject(eq("abc-123"), any(), any(), any(), any())).thenReturn(updated);

        mockMvc.perform(put("/projects/abc-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"description\":\"new desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateProject_notFound_returns404() throws Exception {
        when(projectService.updateProject(eq("missing"), any(), any(), any(), any()))
                .thenThrow(new ProjectNotFoundException("missing"));

        mockMvc.perform(put("/projects/missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\",\"description\":\"D\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // --- DELETE /projects/{id} ---

    @Test
    void deleteProject_found_returns204() throws Exception {
        mockMvc.perform(delete("/projects/abc-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProject_notFound_returns404() throws Exception {
        doThrow(new ProjectNotFoundException("missing"))
                .when(projectService).deleteProject("missing");

        mockMvc.perform(delete("/projects/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Auth tests — stubs added in Task 0, activated in Task 2 once
    // SecurityConfig.java exists and @Import(SecurityConfig.class) is added.
    // TODO: enable after Task 2 (remove @Disabled, wiring is done there)
    // -----------------------------------------------------------------------

    @Test
    @Disabled("TODO: enable after Task 2 — SecurityConfig.java must exist first")
    void createProject_noToken_returns401() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled("TODO: enable after Task 2 — SecurityConfig.java must exist first")
    void createProject_withValidJwt_returns201() throws Exception {
        // jwt() post-processor is added by Task 2 when spring-security-test import is available
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @Disabled("TODO: enable after Task 2 — SecurityConfig.java must exist first")
    void listProjects_noToken_returns200() throws Exception {
        when(projectService.getAllProjects(50)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Disabled("TODO: enable after Task 2 — SecurityConfig.java must exist first")
    void updateProject_noToken_returns401() throws Exception {
        mockMvc.perform(put("/projects/abc-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled("TODO: enable after Task 2 — SecurityConfig.java must exist first")
    void deleteProject_noToken_returns401() throws Exception {
        mockMvc.perform(delete("/projects/abc-123"))
            .andExpect(status().isUnauthorized());
    }
}
