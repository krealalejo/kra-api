package com.kra.api.infrastructure.web.dto;

public class UpdateEducationRequest extends AbstractTimelineRequest {
    private String institution;

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

