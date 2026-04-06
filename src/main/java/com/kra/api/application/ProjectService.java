package com.kra.api.application;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.domain.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(String title, String description, String url, String content) {
        throw new UnsupportedOperationException("Implemented in Phase 7");
    }

    public Optional<Project> getProjectById(String id) {
        throw new UnsupportedOperationException("Implemented in Phase 7");
    }

    public List<Project> getAllProjects() {
        throw new UnsupportedOperationException("Implemented in Phase 7");
    }

    public void updateProject(String id, String title, String description, String url, String content) {
        throw new UnsupportedOperationException("Implemented in Phase 7");
    }

    public void deleteProject(String id) {
        throw new UnsupportedOperationException("Implemented in Phase 7");
    }
}
