package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateSkillCategoryRequest(
        @NotBlank(message = "name is required") String name,
        List<String> skills,
        int sortOrder
) {}
