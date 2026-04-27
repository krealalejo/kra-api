package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDbEducationRepository extends AbstractDynamoDbRepository<Education, EducationDynamoDbItem>
        implements EducationRepository {

    private static final String TYPE_EDUCATION = "TYPE#EDUCATION";

    public DynamoDbEducationRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        super(enhancedClient, tableName, EducationDynamoDbItem.class, "EDUCATION#");
    }

    @Override
    public void save(Education education) {
        save(education, EducationDynamoDbItem::fromDomain);
    }

    @Override
    public Optional<Education> findById(String id) {
        return findById(id, EducationDynamoDbItem::toDomain);
    }

    @Override
    public List<Education> findAll() {
        return findAllByGsi1(TYPE_EDUCATION, EducationDynamoDbItem::toDomain,
                item -> "METADATA".equals(item.getSk()),
                Comparator.comparingInt(Education::getSortOrder));
    }
}
