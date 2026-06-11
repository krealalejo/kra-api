package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateActivityCardRequest(
        @Size(max = 200) String title,
        @Size(max = 500) String description,
        @Size(max = 20) List<@Size(max = 50) String> tags,
        @Size(max = 500) String url
) {}
