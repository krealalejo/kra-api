package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateProjectRequest {

    @NotBlank(message = "title is required")
    private String title;
    private String description;
    private String url;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
