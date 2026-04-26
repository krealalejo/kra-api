package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateSkillCategoryRequest {

    @NotBlank(message = "name is required")
    private String name;
    private List<String> skills;
    private int sortOrder;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
