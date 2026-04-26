package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateEducationRequest extends AbstractTimelineRequest {
    @NotBlank(message = "title is required")
    @Override
    public String getTitle() { return super.getTitle(); }

    private String institution;

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

