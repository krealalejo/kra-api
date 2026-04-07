package com.kra.api.infrastructure.web;

import com.kra.api.application.ProjectNotFoundException;
import com.kra.api.application.ProjectService;
import com.kra.api.domain.model.Project;
import com.kra.api.infrastructure.web.dto.CreateProjectRequest;
import com.kra.api.infrastructure.web.dto.ProjectResponse;
import com.kra.api.infrastructure.web.dto.UpdateProjectRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest req) {
        Project project = projectService.createProject(
                req.getTitle(), req.getDescription(), req.getUrl(), req.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(project));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> list(
            @RequestParam(defaultValue = "50") int limit) {
        int cap = Math.min(Math.max(limit, 0), 100);
        List<ProjectResponse> projects = projectService.getAllProjects(cap).stream()
                .map(ProjectResponse::from)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable String id) {
        return projectService.getProjectById(id)
                .map(p -> ResponseEntity.ok(ProjectResponse.from(p)))
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable String id,
            @Valid @RequestBody UpdateProjectRequest req) {
        Project updated = projectService.updateProject(
                id, req.getTitle(), req.getDescription(), req.getUrl(), req.getContent());
        return ResponseEntity.ok(ProjectResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
