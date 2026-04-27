package com.kra.api.domain.repository;

import com.kra.api.domain.model.ProjectMetadata;

public interface ProjectMetadataRepository {

    ProjectMetadata findByOwnerAndRepo(String owner, String repo);

    void save(String owner, String repo, ProjectMetadata metadata);
}
