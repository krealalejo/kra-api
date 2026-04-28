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

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
