package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProjectRequest(
        @NotBlank(message = "title is required") String title,
        String description,
        String url,
        String content
) {}
