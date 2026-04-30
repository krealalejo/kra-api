package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.SkillCategory;

import java.util.List;

public record SkillCategoryResponse(String id, String name, List<String> skills, int sortOrder) {

    public static SkillCategoryResponse from(SkillCategory cat) {
        return new SkillCategoryResponse(cat.getId(), cat.getName(), cat.getSkills(), cat.getSortOrder());
    }
}
