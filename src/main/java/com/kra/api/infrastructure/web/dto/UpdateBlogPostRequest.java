package com.kra.api.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateBlogPostRequest {

    @NotBlank
    private String title;

    @Size(max = 200_000)
    private String content;

    @Valid
    private List<ReferenceRequest> references;

    private String imageUrl;

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
