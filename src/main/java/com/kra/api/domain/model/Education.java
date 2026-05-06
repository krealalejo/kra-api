package com.kra.api.domain.model;

import java.io.Serial;

@SuppressWarnings("java:S2160")
public class Education extends TimelineEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String institution;

    public Education(String id, String title, String institution, String location,
                     String years, String description, int sortOrder) {
        super(id, title, location, years, description, sortOrder);
        this.institution = institution;
    }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

