package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpsertProjectMetadataRequest(
        String role,
        String year,
        String kind,
        String mainBranch,
        @Size(max = 30) List<@Size(max = 50) String> stack
) {}
