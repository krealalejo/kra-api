package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Experience;
import com.kra.api.domain.repository.ExperienceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDbExperienceRepository extends AbstractDynamoDbRepository<Experience, ExperienceDynamoDbItem>
        implements ExperienceRepository {

    private static final String TYPE_EXPERIENCE = "TYPE#EXPERIENCE";

    public DynamoDbExperienceRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        super(enhancedClient, tableName, ExperienceDynamoDbItem.class, "EXPERIENCE#");
    }

    @Override
    public void save(Experience experience) {
        save(experience, ExperienceDynamoDbItem::fromDomain);
    }

    @Override
    public Optional<Experience> findById(String id) {
        return findById(id, ExperienceDynamoDbItem::toDomain);
    }

    @Override
    public List<Experience> findAll() {
        return findAllByGsi1(TYPE_EXPERIENCE, ExperienceDynamoDbItem::toDomain,
                item -> "METADATA".equals(item.getSk()),
                Comparator.comparingInt(Experience::getSortOrder));
    }
}
