package com.kra.api.application;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @CacheEvict(value = "education", allEntries = true)
    public Education create(String title, String institution, String location,
                            String years, String description, int sortOrder, String logoUrl) {
        Education education = new Education(
                UUID.randomUUID().toString(), title, institution, location, years, description, sortOrder);
        education.setLogoUrl(logoUrl);
        educationRepository.save(education);
        return education;
    }

    @Cacheable("education")
    public List<Education> findAll() {
        return educationRepository.findAll();
    }

    @CacheEvict(value = "education", allEntries = true)
    public Education update(String id, EducationChanges changes) {
        Education existing = educationRepository.findById(id)
                .orElseThrow(() -> new EducationNotFoundException(id));
        if (changes.title() != null) existing.setTitle(changes.title());
        if (changes.institution() != null) existing.setInstitution(changes.institution());
        if (changes.location() != null) existing.setLocation(changes.location());
        if (changes.years() != null) existing.setYears(changes.years());
        if (changes.description() != null) existing.setDescription(changes.description());
        if (changes.sortOrder() != null) existing.setSortOrder(changes.sortOrder());
        if (changes.updateLogoUrl()) existing.setLogoUrl(changes.logoUrl());
        educationRepository.save(existing);
        return existing;
    }

    @CacheEvict(value = "education", allEntries = true)
    public void delete(String id) {
        educationRepository.findById(id)
                .orElseThrow(() -> new EducationNotFoundException(id));
        educationRepository.deleteById(id);
    }
}
