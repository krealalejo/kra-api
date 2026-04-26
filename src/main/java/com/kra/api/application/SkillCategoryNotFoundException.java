package com.kra.api.application;

public class SkillCategoryNotFoundException extends EntityNotFoundException {

    public SkillCategoryNotFoundException(String id) {
        super("Skill category not found: " + id);
    }
}
