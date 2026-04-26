package com.kra.api.domain.repository;

import com.kra.api.domain.model.ActivityCard;
import java.util.List;
import java.util.Optional;

public interface ActivityCardRepository {
    List<ActivityCard> findAll();
    Optional<ActivityCard> findByType(String type);
    void save(ActivityCard card);
}
