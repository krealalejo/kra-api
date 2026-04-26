package com.kra.api.domain.model;

import java.util.Objects;

public class Education {

    private final String id;
    private String title;
    private String institution;
    private String location;
    private String years;
    private String description;
    private int sortOrder;

    public Education(String id, String title, String institution, String location,
                     String years, String description, int sortOrder) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.institution = institution;
        this.location = location;
        this.years = years;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Education e)) return false;
        return Objects.equals(id, e.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
