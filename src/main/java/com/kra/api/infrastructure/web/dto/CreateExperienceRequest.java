package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateExperienceRequest extends AbstractTimelineRequest {
    @NotBlank(message = "title is required")
    @Override
    public String getTitle() { return super.getTitle(); }

    private String company;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

