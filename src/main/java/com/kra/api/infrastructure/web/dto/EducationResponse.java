package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.Education;

public class EducationResponse {

    private String id;
    private String title;
    private String institution;
    private String location;
    private String years;
    private String description;
    private int sortOrder;

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getYears() { return years; }
    public void setYears(String years) { this.years = years; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
