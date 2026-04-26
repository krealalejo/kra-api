package com.kra.api.domain.model;

public class Experience extends TimelineEntity {
    private String company;

    public Experience(String id, String title, String company, String location,
                      String years, String description, int sortOrder) {
        super(id, title, location, years, description, sortOrder);
        this.company = company;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

