package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UploadRequest {

    @NotBlank
    private String filename;

    @NotBlank
    @Pattern(
        regexp = "^(image/jpeg|image/png|image/gif|image/webp|application/pdf)$",
        message = "contentType must be one of: image/jpeg, image/png, image/gif, image/webp, application/pdf"
    )
    private String contentType;

    @Pattern(
        regexp = "^(portrait|blog|pdf|logo)$",
        message = "uploadType must be one of: portrait, blog, pdf, logo"
    )
    private String uploadType;

    @Pattern(
        regexp = "^[a-z0-9-]{1,128}$",
        message = "entitySlug must be lowercase alphanumeric + hyphens (1-128 chars)"
    )
    private String entitySlug;

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getUploadType() { return uploadType; }
    public void setUploadType(String uploadType) { this.uploadType = uploadType; }

    public String getEntitySlug() { return entitySlug; }
    public void setEntitySlug(String entitySlug) { this.entitySlug = entitySlug; }
}
