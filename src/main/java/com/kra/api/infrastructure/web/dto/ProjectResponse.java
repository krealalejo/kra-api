package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.Project;

public class ProjectResponse {

    private String id;
    private String title;
    private String description;
    private String url;
    private String content;

    public static ProjectResponse from(Project project) {
        ProjectResponse r = new ProjectResponse();
        r.setId(project.getId().getValue());
        r.setTitle(project.getTitle());
        r.setDescription(project.getDescription());
        r.setUrl(project.getUrl());
        r.setContent(project.getContent());
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
