package com.kra.api.domain.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    void save(Project project);

    Optional<Project> findById(ProjectId id);

    List<Project> findAll();

    void deleteById(ProjectId id);
}
