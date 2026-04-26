package com.kra.api.domain.model;

import java.util.Objects;

public abstract class TimelineEntity {
    protected final String id;
    protected String title;
    protected String location;
    protected String years;
    protected String description;
    protected int sortOrder;

    protected TimelineEntity(String id, String title, String location, String years, String description, int sortOrder) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
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
        if (o == null || getClass() != o.getClass()) return false;
        TimelineEntity that = (TimelineEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
