package com.kra.api.domain.model;

import java.util.Objects;

public class Experience {

    private final String id;
    private String title;
    private String company;
    private String location;
    private String years;
    private String description;
    private int sortOrder;

    public Experience(String id, String title, String company, String location,
                      String years, String description, int sortOrder) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.company = company;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Experience e)) return false;
        return Objects.equals(id, e.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
