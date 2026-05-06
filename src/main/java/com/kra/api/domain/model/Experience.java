package com.kra.api.domain.model;

import java.io.Serial;

@SuppressWarnings("java:S2160")
public class Experience extends TimelineEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String company;

    public Experience(String id, String title, String company, String location,
                      String years, String description, int sortOrder) {
        super(id, title, location, years, description, sortOrder);
        this.company = company;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}

