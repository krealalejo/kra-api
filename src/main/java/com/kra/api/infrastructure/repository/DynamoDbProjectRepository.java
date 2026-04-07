package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.domain.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Infrastructure adapter: implements the ProjectRepository domain port using
 * AWS SDK v2 DynamoDB Enhanced Client.
 *
 * Key design (matches kra-table from Phase 3 Terraform + ProjectDynamoDbItem):
 *   PK  = "PROJECT#<id>"  (base table partition key)
 *   SK  = "METADATA"      (base table sort key)
 *   GSI1PK = "TYPE#PROJECT"  (GSI1 partition key — used for findAll)
 *
 * Spring picks this up as the single @Repository implementation of ProjectRepository.
 * ProjectService (Phase 5) will have this bean injected automatically.
 */
@Repository
public class DynamoDbProjectRepository implements ProjectRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_PROJECT = "TYPE#PROJECT";

    private final DynamoDbTable<ProjectDynamoDbItem> table;

    public DynamoDbProjectRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ProjectDynamoDbItem.class));
    }

    @Override
    public void save(Project project) {
        table.putItem(ProjectDynamoDbItem.fromDomain(project));
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        Key key = Key.builder()
                .partitionValue("PROJECT#" + id.getValue())
                .sortValue("METADATA")
                .build();
        ProjectDynamoDbItem item = table.getItem(key);
        return Optional.ofNullable(item).map(ProjectDynamoDbItem::toDomain);
    }

    @Override
    public List<Project> findAll() {
        DynamoDbIndex<ProjectDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional
                .keyEqualTo(k -> k.partitionValue(TYPE_PROJECT));
        return StreamSupport.stream(
                        gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .map(ProjectDynamoDbItem::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ProjectId id) {
        Key key = Key.builder()
                .partitionValue("PROJECT#" + id.getValue())
                .sortValue("METADATA")
                .build();
        table.deleteItem(key);
    }
}
