package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class DynamoDbEducationRepository implements EducationRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_EDUCATION = "TYPE#EDUCATION";

    private final DynamoDbTable<EducationDynamoDbItem> table;

    public DynamoDbEducationRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(EducationDynamoDbItem.class));
    }

    @Override
    public void save(Education education) {
        table.putItem(EducationDynamoDbItem.fromDomain(education));
    }

    @Override
    public Optional<Education> findById(String id) {
        Key key = Key.builder()
                .partitionValue("EDUCATION#" + id)
                .sortValue("METADATA")
                .build();
        EducationDynamoDbItem item = table.getItem(key);
        return Optional.ofNullable(item).map(EducationDynamoDbItem::toDomain);
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

    @Override
    public void deleteById(String id) {
        Key key = Key.builder()
                .partitionValue("EDUCATION#" + id)
                .sortValue("METADATA")
                .build();
        table.deleteItem(key);
    }
}
