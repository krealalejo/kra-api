package com.kra.api.application;

import com.kra.api.domain.model.ActivityCard;
import com.kra.api.domain.repository.ActivityCardRepository;
import com.kra.api.infrastructure.web.dto.ActivityCardResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ActivityCardService {

    private final ActivityCardRepository repository;

    public ActivityCardService(ActivityCardRepository repository) {
        this.repository = repository;
    }

    @Cacheable("activity")
    public List<ActivityCardResponse> getAll() {
        return repository.findAll().stream()
                .map(c -> new ActivityCardResponse(c.type(), c.title(), c.description(), c.tags()))
                .toList();
    }

    @CacheEvict(value = "activity", allEntries = true)
    public ActivityCardResponse update(String type, String title, String description, List<String> tags) {
        ActivityCard existing = repository.findByType(type)
                .orElse(new ActivityCard(type.toUpperCase(), null, null, null));

        ActivityCard updated = new ActivityCard(
                existing.type(),
                title != null && !title.isBlank() ? title : existing.title(),
                description != null && !description.isBlank() ? description : existing.description(),
                tags != null ? tags : existing.tags()
        );
        repository.save(updated);
        return new ActivityCardResponse(updated.type(), updated.title(), updated.description(), updated.tags());
    }
}
