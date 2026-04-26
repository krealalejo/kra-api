package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.Education;

public class EducationResponse extends AbstractTimelineResponse {
    private String institution;

    public static EducationResponse from(Education edu) {
        EducationResponse r = new EducationResponse();
        r.setId(edu.getId());
        r.setTitle(edu.getTitle());
        r.setInstitution(edu.getInstitution());
        r.setLocation(edu.getLocation());
        r.setYears(edu.getYears());
        r.setDescription(edu.getDescription());
        r.setSortOrder(edu.getSortOrder());
        return r;
    }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

