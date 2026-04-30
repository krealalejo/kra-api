package com.kra.api.application;

import com.kra.api.domain.model.ProjectMetadata;
import com.kra.api.domain.repository.ProjectMetadataRepository;
import com.kra.api.infrastructure.web.dto.ProjectMetadataResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMetadataService {

    private final ProjectMetadataRepository repository;

    public ProjectMetadataService(ProjectMetadataRepository repository) {
        this.repository = repository;
    }

    public ProjectMetadataResponse getMetadata(String owner, String repo) {
        ProjectMetadata m = repository.findByOwnerAndRepo(owner, repo);
        return new ProjectMetadataResponse(m.role(), m.year(), m.kind(), m.mainBranch(), m.stack());
    }

    public ProjectMetadataResponse upsertMetadata(String owner, String repo, String role, String year,
                                                   String kind, String mainBranch, List<String> stack) {
        ProjectMetadata updated = new ProjectMetadata(role, year, kind, mainBranch, stack);
        repository.save(owner, repo, updated);
        return new ProjectMetadataResponse(role, year, kind, mainBranch, stack);
    }
}
