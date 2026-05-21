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
    public Experience update(String id, String title, String company, String location,
                             String years, String description, Integer sortOrder, String logoUrl, boolean updateLogoUrl) {
        Experience existing = experienceRepository.findById(id)
                .orElseThrow(() -> new ExperienceNotFoundException(id));
        if (title != null) existing.setTitle(title);
        if (company != null) existing.setCompany(company);
        if (location != null) existing.setLocation(location);
        if (years != null) existing.setYears(years);
        if (description != null) existing.setDescription(description);
        if (sortOrder != null) existing.setSortOrder(sortOrder);
        if (updateLogoUrl) existing.setLogoUrl(logoUrl);
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
