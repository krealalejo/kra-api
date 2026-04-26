package com.kra.api.application;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    public SkillCategoryService(SkillCategoryRepository skillCategoryRepository) {
        this.skillCategoryRepository = skillCategoryRepository;
    }

    public SkillCategory create(String name, List<String> skills, int sortOrder) {
        SkillCategory category = new SkillCategory(
                UUID.randomUUID().toString(), name, skills, sortOrder);
        skillCategoryRepository.save(category);
        return category;
    }

    public List<SkillCategory> findAll() {
        return skillCategoryRepository.findAll();
    }

    public SkillCategory update(String id, String name, List<String> skills, Integer sortOrder) {
        SkillCategory existing = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new SkillCategoryNotFoundException(id));
        if (name != null) existing.setName(name);
        if (skills != null) existing.setSkills(skills);
        if (sortOrder != null) existing.setSortOrder(sortOrder);
        skillCategoryRepository.save(existing);
        return existing;
    }

    public void delete(String id) {
        skillCategoryRepository.findById(id)
                .orElseThrow(() -> new SkillCategoryNotFoundException(id));
        skillCategoryRepository.deleteById(id);
    }
}
