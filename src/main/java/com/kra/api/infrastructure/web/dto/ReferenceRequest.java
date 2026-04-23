package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ReferenceRequest(
        @NotBlank String label,
        @URL String url) {}
