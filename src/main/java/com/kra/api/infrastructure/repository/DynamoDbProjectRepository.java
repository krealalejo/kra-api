package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.domain.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class DynamoDbProjectRepository extends AbstractDynamoDbRepository<Project, ProjectDynamoDbItem>
        implements ProjectRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_PROJECT = "TYPE#PROJECT";

    public DynamoDbProjectRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        super(enhancedClient, tableName, ProjectDynamoDbItem.class, "PROJECT#");
    }

    @Override
    public void save(Project project) {
        save(project, ProjectDynamoDbItem::fromDomain);
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return findById(id.getValue(), ProjectDynamoDbItem::toDomain);
    }

    @Override
    public List<Project> findAll() {
        DynamoDbIndex<ProjectDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(TYPE_PROJECT));
        return StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .map(ProjectDynamoDbItem::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ProjectId id) {
        deleteById(id.getValue());
    }
}
