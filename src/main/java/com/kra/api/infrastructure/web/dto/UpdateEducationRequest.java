package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;

public class UpdateEducationRequest extends AbstractTimelineRequest {
    @Pattern(regexp = "(?s).*\\S.*", message = "title must not be blank")
    @Override
    public String getTitle() { return super.getTitle(); }

    private String institution;

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

