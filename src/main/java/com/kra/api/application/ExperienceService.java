package com.kra.api.application;

import com.kra.api.domain.model.Experience;
import com.kra.api.domain.repository.ExperienceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @CacheEvict(value = "experience", allEntries = true)
    public Experience create(String title, String company, String location,
                             String years, String description, int sortOrder, String logoUrl) {
        Experience experience = new Experience(
                UUID.randomUUID().toString(), title, company, location, years, description, sortOrder);
        experience.setLogoUrl(logoUrl);
        experienceRepository.save(experience);
        return experience;
    }

    @Cacheable("experience")
    public List<Experience> findAll() {
        return experienceRepository.findAll();
    }

    @CacheEvict(value = "experience", allEntries = true)
    public Experience update(String id, ExperienceChanges changes) {
        Experience existing = experienceRepository.findById(id)
                .orElseThrow(() -> new ExperienceNotFoundException(id));
        if (changes.title() != null) existing.setTitle(changes.title());
        if (changes.company() != null) existing.setCompany(changes.company());
        if (changes.location() != null) existing.setLocation(changes.location());
        if (changes.years() != null) existing.setYears(changes.years());
        if (changes.description() != null) existing.setDescription(changes.description());
        if (changes.sortOrder() != null) existing.setSortOrder(changes.sortOrder());
        if (changes.updateLogoUrl()) existing.setLogoUrl(changes.logoUrl());
        experienceRepository.save(existing);
        return existing;
    }

    @CacheEvict(value = "experience", allEntries = true)
    public void delete(String id) {
        experienceRepository.findById(id)
                .orElseThrow(() -> new ExperienceNotFoundException(id));
        experienceRepository.deleteById(id);
    }
}
