package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.Experience;

public class ExperienceResponse extends AbstractTimelineResponse {
    private String company;

    public static ExperienceResponse from(Experience exp) {
        ExperienceResponse r = new ExperienceResponse();
        r.setId(exp.getId());
        r.setTitle(exp.getTitle());
        r.setCompany(exp.getCompany());
        r.setLocation(exp.getLocation());
        r.setYears(exp.getYears());
        r.setDescription(exp.getDescription());
        r.setSortOrder(exp.getSortOrder());
        return r;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

