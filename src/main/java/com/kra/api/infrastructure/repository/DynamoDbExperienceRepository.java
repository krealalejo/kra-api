package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Experience;
import com.kra.api.domain.repository.ExperienceRepository;
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
public class DynamoDbExperienceRepository implements ExperienceRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_EXPERIENCE = "TYPE#EXPERIENCE";

    private final DynamoDbTable<ExperienceDynamoDbItem> table;

    public DynamoDbExperienceRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ExperienceDynamoDbItem.class));
    }

    @Override
    public void save(Experience experience) {
        table.putItem(ExperienceDynamoDbItem.fromDomain(experience));
    }

    @Override
    public Optional<Experience> findById(String id) {
        Key key = Key.builder()
                .partitionValue("EXPERIENCE#" + id)
                .sortValue("METADATA")
                .build();
        ExperienceDynamoDbItem item = table.getItem(key);
        return Optional.ofNullable(item).map(ExperienceDynamoDbItem::toDomain);
    }

    @Override
    public List<Experience> findAll() {
        DynamoDbIndex<ExperienceDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(TYPE_EXPERIENCE));
        return StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(item -> "METADATA".equals(item.getSk()))
                .map(ExperienceDynamoDbItem::toDomain)
                .sorted(Comparator.comparingInt(Experience::getSortOrder))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        Key key = Key.builder()
                .partitionValue("EXPERIENCE#" + id)
                .sortValue("METADATA")
                .build();
        table.deleteItem(key);
    }
}
