package com.kra.api.domain.model;

import java.time.Instant;
import java.util.Objects;

public class BlogPost {

    private final BlogSlug slug;
    private String title;
    private String content;
    private final Instant createdAt;
    private Instant updatedAt;

    public BlogPost(BlogSlug slug, String title, String content, Instant createdAt, Instant updatedAt) {
        this.slug = Objects.requireNonNull(slug, "slug must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.content = content != null ? content : "";
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public BlogSlug getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    public void setContent(String content) {
        this.content = content != null ? content : "";
    }

    public void touchUpdatedAt(Instant now) {
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }
}
