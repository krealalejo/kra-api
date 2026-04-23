package com.kra.api.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateBlogPostRequest {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9-]{1,128}$", message = "slug must match ^[a-z0-9-]{1,128}$")
    private String slug;

    @NotBlank
    private String title;

    @Size(max = 200_000)
    private String content;

    @Valid
    private List<ReferenceRequest> references;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ReferenceRequest> getReferences() { return references; }
    public void setReferences(List<ReferenceRequest> references) { this.references = references; }
}
