package com.kra.api.infrastructure.web.dto;

import java.util.List;

public class UpdateSkillCategoryRequest {

    private String name;
    private List<String> skills;
    private Integer sortOrder;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
