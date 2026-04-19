package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateBlogPostRequest {

    @NotBlank
    private String title;

    @Size(max = 200_000)
    private String content;

    private List<BlogPostResponse.ReferenceDto> references;

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

    public List<BlogPostResponse.ReferenceDto> getReferences() { return references; }
    public void setReferences(List<BlogPostResponse.ReferenceDto> references) { this.references = references; }
}
