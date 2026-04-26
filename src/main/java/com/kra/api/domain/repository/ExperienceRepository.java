package com.kra.api.domain.repository;

import com.kra.api.domain.model.Experience;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {

    void save(Experience experience);

    Optional<Experience> findById(String id);

    List<Experience> findAll();

    void deleteById(String id);
}
