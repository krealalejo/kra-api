package com.kra.api.application;

public class SkillCategoryNotFoundException extends RuntimeException {

    public SkillCategoryNotFoundException(String id) {
        super("Skill category not found: " + id);
    }
}
