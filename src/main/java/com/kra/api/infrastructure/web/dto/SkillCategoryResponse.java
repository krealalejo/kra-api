package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.SkillCategory;

import java.util.List;

public class SkillCategoryResponse {

    private String id;
    private String name;
    private List<String> skills;
    private int sortOrder;

    public static SkillCategoryResponse from(SkillCategory cat) {
        SkillCategoryResponse r = new SkillCategoryResponse();
        r.setId(cat.getId());
        r.setName(cat.getName());
        r.setSkills(cat.getSkills());
        r.setSortOrder(cat.getSortOrder());
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
