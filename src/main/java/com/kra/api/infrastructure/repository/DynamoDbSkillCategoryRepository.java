package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
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
public class DynamoDbSkillCategoryRepository extends AbstractDynamoDbRepository<SkillCategory, SkillCategoryDynamoDbItem>
        implements SkillCategoryRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_SKILL = "TYPE#SKILL";

    public DynamoDbSkillCategoryRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        super(enhancedClient, tableName, SkillCategoryDynamoDbItem.class, "SKILL#");
    }

    @Override
    public void save(SkillCategory skillCategory) {
        save(skillCategory, SkillCategoryDynamoDbItem::fromDomain);
    }

    @Override
    public Optional<SkillCategory> findById(String id) {
        return findById(id, SkillCategoryDynamoDbItem::toDomain);
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
}
