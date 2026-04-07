package com.kra.api.application;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.domain.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String title, String description, String url, String content) {
        Project project = new Project(
                ProjectId.of(UUID.randomUUID().toString()),
                title, description, url, content
        );
        projectRepository.save(project);
        return project;
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(ProjectId.of(id));
    }

    public List<Project> getAllProjects(int limit) {
        return projectRepository.findAll().stream()
                .limit(limit)
                .toList();
    }

    public Project updateProject(String id, String title, String description, String url, String content) {
        Project project = projectRepository.findById(ProjectId.of(id))
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.setTitle(title);
        project.setDescription(description);
        project.setUrl(url);
        project.setContent(content);
        projectRepository.save(project);
        return project;
    }

    public void deleteProject(String id) {
        Project found = projectRepository.findById(ProjectId.of(id))
                .orElseThrow(() -> new ProjectNotFoundException(id));
        projectRepository.deleteById(found.getId());
    }
}
