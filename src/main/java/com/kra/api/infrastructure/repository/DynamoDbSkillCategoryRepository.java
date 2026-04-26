package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
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
public class DynamoDbSkillCategoryRepository implements SkillCategoryRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_SKILL = "TYPE#SKILL";

    private final DynamoDbTable<SkillCategoryDynamoDbItem> table;

    public DynamoDbSkillCategoryRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(SkillCategoryDynamoDbItem.class));
    }

    @Override
    public void save(SkillCategory skillCategory) {
        table.putItem(SkillCategoryDynamoDbItem.fromDomain(skillCategory));
    }

    @Override
    public Optional<SkillCategory> findById(String id) {
        Key key = Key.builder()
                .partitionValue("SKILL#" + id)
                .sortValue("METADATA")
                .build();
        SkillCategoryDynamoDbItem item = table.getItem(key);
        return Optional.ofNullable(item).map(SkillCategoryDynamoDbItem::toDomain);
    }

    @Override
    public List<SkillCategory> findAll() {
        DynamoDbIndex<SkillCategoryDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(TYPE_SKILL));
        return StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(item -> "METADATA".equals(item.getSk()))
                .map(SkillCategoryDynamoDbItem::toDomain)
                .sorted(Comparator.comparingInt(SkillCategory::getSortOrder))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        Key key = Key.builder()
                .partitionValue("SKILL#" + id)
                .sortValue("METADATA")
                .build();
        table.deleteItem(key);
    }
}
