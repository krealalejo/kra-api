package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateBlogPostRequest extends UpdateBlogPostRequest {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9-]{1,128}$", message = "slug must match ^[a-z0-9-]{1,128}$")
    private String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
