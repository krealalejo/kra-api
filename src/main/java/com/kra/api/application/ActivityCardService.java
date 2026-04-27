package com.kra.api.application;

import com.kra.api.domain.model.ActivityCard;
import com.kra.api.domain.repository.ActivityCardRepository;
import com.kra.api.infrastructure.web.dto.ActivityCardResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityCardService {

    private final ActivityCardRepository repository;

    public ActivityCardService(ActivityCardRepository repository) {
        this.repository = repository;
    }

    public List<ActivityCardResponse> getAll() {
        return repository.findAll().stream()
                .map(c -> new ActivityCardResponse(c.getType(), c.getTitle(), c.getDescription(), c.getTags()))
                .toList();
    }

    public ActivityCardResponse update(String type, String title, String description, List<String> tags) {
        ActivityCard card = repository.findByType(type)
                .orElse(new ActivityCard(type.toUpperCase(), null, null, null));

        if (title != null && !title.isBlank())             card.setTitle(title);
        if (description != null && !description.isBlank()) card.setDescription(description);
        if (tags != null)                                   card.setTags(tags);

        repository.save(card);
        return new ActivityCardResponse(card.getType(), card.getTitle(), card.getDescription(), card.getTags());
    }
}
