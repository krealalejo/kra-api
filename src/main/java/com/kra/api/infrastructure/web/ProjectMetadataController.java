package com.kra.api.infrastructure.web;

import com.kra.api.application.ProjectMetadataService;
import com.kra.api.infrastructure.web.dto.ProjectMetadataResponse;
import com.kra.api.infrastructure.web.dto.UpsertProjectMetadataRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/projects/metadata")
public class ProjectMetadataController {

    private static final Set<String> ALLOWED_KINDS = Set.of(
            "Backend", "Frontend", "Fullstack", "Infrastructure", "Library", "CLI", "Serverless"
    );

    private static final String PATH_VARIABLE_PATTERN = "^[a-zA-Z0-9_.\\-]+$";

    private final ProjectMetadataService projectMetadataService;

    public ProjectMetadataController(ProjectMetadataService projectMetadataService) {
        this.projectMetadataService = projectMetadataService;
    }

    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<ProjectMetadataResponse> getMetadata(
            @PathVariable String owner,
            @PathVariable String repo) {
        validatePathVariable(owner);
        validatePathVariable(repo);
        ProjectMetadataResponse result = projectMetadataService.getMetadata(owner, repo);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{owner}/{repo}")
    public ResponseEntity<ProjectMetadataResponse> upsertMetadata(
            @PathVariable String owner,
            @PathVariable String repo,
            @Valid @RequestBody UpsertProjectMetadataRequest request) {
        validatePathVariable(owner);
        validatePathVariable(repo);
        String kind = request.getKind();
        if (kind != null && !kind.isBlank() && !ALLOWED_KINDS.contains(kind)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid kind value");
        }
        ProjectMetadataResponse result = projectMetadataService.upsertMetadata(
                owner, repo,
                request.getRole(), request.getYear(), request.getKind(),
                request.getMainBranch(), request.getStack()
        );
        return ResponseEntity.ok(result);
    }

    private void validatePathVariable(String value) {
        if (value == null || !value.matches(PATH_VARIABLE_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path variable: " + value);
        }
    }
}
