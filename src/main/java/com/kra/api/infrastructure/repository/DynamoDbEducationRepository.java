package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class DynamoDbEducationRepository extends AbstractDynamoDbRepository<Education, EducationDynamoDbItem>
        implements EducationRepository {

    private static final String GSI1_NAME = "GSI1";
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
        DynamoDbIndex<EducationDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(TYPE_EDUCATION));
        return StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(item -> "METADATA".equals(item.getSk()))
                .map(EducationDynamoDbItem::toDomain)
                .sorted(Comparator.comparingInt(Education::getSortOrder))
                .toList();
    }
}
