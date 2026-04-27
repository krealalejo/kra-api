package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDbSkillCategoryRepository extends AbstractDynamoDbRepository<SkillCategory, SkillCategoryDynamoDbItem>
        implements SkillCategoryRepository {

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
        return findAllByGsi1(TYPE_SKILL, SkillCategoryDynamoDbItem::toDomain,
                item -> "METADATA".equals(item.getSk()),
                Comparator.comparingInt(SkillCategory::getSortOrder));
    }
}
