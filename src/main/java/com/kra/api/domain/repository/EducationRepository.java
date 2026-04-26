package com.kra.api.domain.repository;

import com.kra.api.domain.model.Education;

import java.util.List;
import java.util.Optional;

public interface EducationRepository {

    void save(Education education);

    Optional<Education> findById(String id);

    List<Education> findAll();

    void deleteById(String id);
}
