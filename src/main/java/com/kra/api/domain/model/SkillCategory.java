package com.kra.api.domain.model;

import java.util.List;
import java.util.Objects;

public class SkillCategory {

    private final String id;
    private String name;
    private List<String> skills;
    private int sortOrder;

    public SkillCategory(String id, String name, List<String> skills, int sortOrder) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.skills = skills != null ? List.copyOf(skills) : List.of();
        this.sortOrder = sortOrder;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) {
        this.skills = skills != null ? List.copyOf(skills) : List.of();
    }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillCategory s)) return false;
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
