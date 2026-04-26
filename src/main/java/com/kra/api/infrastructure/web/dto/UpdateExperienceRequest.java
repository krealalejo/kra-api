package com.kra.api.infrastructure.web.dto;

public class UpdateExperienceRequest extends AbstractTimelineRequest {
    private String company;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

