package com.kra.api.domain.model;

import java.util.Objects;

public class Project {

    private final ProjectId id;
    private String title;
    private String description;
    private String url;
    private String content;

    public Project(ProjectId id, String title, String description, String url, String content) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.url = url;
        this.content = content;
    }

    public ProjectId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getContent() { return content; }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }
    public void setDescription(String description) { this.description = description; }
    public void setUrl(String url) { this.url = url; }
    public void setContent(String content) { this.content = content; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
