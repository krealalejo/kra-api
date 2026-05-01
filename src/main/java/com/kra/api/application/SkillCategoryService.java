package com.kra.api.application;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    public SkillCategoryService(SkillCategoryRepository skillCategoryRepository) {
        this.skillCategoryRepository = skillCategoryRepository;
    }

    @CacheEvict(value = "skillCategories", allEntries = true)
    public SkillCategory create(String name, List<String> skills, int sortOrder) {
        SkillCategory category = new SkillCategory(
                UUID.randomUUID().toString(), name, skills, sortOrder);
        skillCategoryRepository.save(category);
        return category;
    }

    @Cacheable("skillCategories")
    public List<SkillCategory> findAll() {
        return skillCategoryRepository.findAll();
    }

    @CacheEvict(value = "skillCategories", allEntries = true)
    public SkillCategory update(String id, String name, List<String> skills, Integer sortOrder) {
        SkillCategory existing = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new SkillCategoryNotFoundException(id));
        if (name != null) existing.setName(name);
        if (skills != null) existing.setSkills(skills);
        if (sortOrder != null) existing.setSortOrder(sortOrder);
        skillCategoryRepository.save(existing);
        return existing;
    }

    @CacheEvict(value = "skillCategories", allEntries = true)
    public void delete(String id) {
        skillCategoryRepository.findById(id)
                .orElseThrow(() -> new SkillCategoryNotFoundException(id));
        skillCategoryRepository.deleteById(id);
    }
}
