package com.kra.api.domain.repository;

import com.kra.api.domain.model.SkillCategory;

import java.util.List;
import java.util.Optional;

public interface SkillCategoryRepository {

    void save(SkillCategory skillCategory);

    Optional<SkillCategory> findById(String id);

    List<SkillCategory> findAll();

    void deleteById(String id);
}
