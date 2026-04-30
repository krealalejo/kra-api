package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.Project;

public record ProjectResponse(String id, String title, String description, String url, String content) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId().getValue(),
                project.getTitle(),
                project.getDescription(),
                project.getUrl(),
                project.getContent()
        );
    }
}
