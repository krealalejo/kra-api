package com.kra.api.application;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    public Education create(String title, String institution, String location,
                            String years, String description, int sortOrder) {
        Education education = new Education(
                UUID.randomUUID().toString(), title, institution, location, years, description, sortOrder);
        educationRepository.save(education);
        return education;
    }

    public List<Education> findAll() {
        return educationRepository.findAll();
    }

    public Education update(String id, String title, String institution, String location,
                            String years, String description, Integer sortOrder) {
        Education existing = educationRepository.findById(id)
                .orElseThrow(() -> new EducationNotFoundException(id));
        if (title != null) existing.setTitle(title);
        if (institution != null) existing.setInstitution(institution);
        if (location != null) existing.setLocation(location);
        if (years != null) existing.setYears(years);
        if (description != null) existing.setDescription(description);
        if (sortOrder != null) existing.setSortOrder(sortOrder);
        educationRepository.save(existing);
        return existing;
    }

    public void delete(String id) {
        educationRepository.findById(id)
                .orElseThrow(() -> new EducationNotFoundException(id));
        educationRepository.deleteById(id);
    }
}
