package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;

public class UpdateExperienceRequest extends AbstractTimelineRequest {
    @Pattern(regexp = "(?s).*\\S.*", message = "title must not be blank")
    @Override
    public String getTitle() { return super.getTitle(); }

    private String company;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

