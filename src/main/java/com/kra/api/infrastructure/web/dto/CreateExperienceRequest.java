package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateExperienceRequest {

    @NotBlank(message = "title is required")
    private String title;
    private String company;
    private String location;
    private String years;
    private String description;
    private int sortOrder;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getYears() { return years; }
    public void setYears(String years) { this.years = years; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
