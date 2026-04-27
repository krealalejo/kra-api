package com.kra.api.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class BlogPost {

    private final BlogSlug slug;
    private String title;
    private String content;
    private final Instant createdAt;
    private Instant updatedAt;
    private List<Reference> references;
    private String imageUrl;

    public BlogPost(BlogSlug slug, String title, String content,
                    Instant createdAt, Instant updatedAt,
                    List<Reference> references, String imageUrl) {
        this.slug = Objects.requireNonNull(slug, "slug must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.content = content != null ? content : "";
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.references = references != null ? List.copyOf(references) : List.of();
        this.imageUrl = imageUrl;
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

    public List<Reference> getReferences() {
        return references;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    public void setContent(String content) {
        this.content = content != null ? content : "";
    }

    public void setReferences(List<Reference> references) {
        this.references = references != null ? List.copyOf(references) : List.of();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void touchUpdatedAt(Instant now) {
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }
}
