package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.ProjectMetadata;
import com.kra.api.domain.repository.ProjectMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DynamoDbProjectMetadataRepository implements ProjectMetadataRepository {

    private static final String SK = "METADATA";

    private final DynamoDbTable<ProjectMetadataDynamoDbItem> table;

    public DynamoDbProjectMetadataRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ProjectMetadataDynamoDbItem.class));
    }

    @Override
    public ProjectMetadata findByOwnerAndRepo(String owner, String repo) {
        String pk = "METADATA#" + owner + "#" + repo;
        Key key = Key.builder().partitionValue(pk).sortValue(SK).build();
        ProjectMetadataDynamoDbItem item = table.getItem(key);
        if (item == null) {
            return new ProjectMetadata(null, null, null, null, null);
        }
        return new ProjectMetadata(item.getRole(), item.getYear(), item.getKind(), item.getMainBranch(), item.getStack());
    }

    @Override
    public void save(String owner, String repo, ProjectMetadata metadata) {
        String pk = "METADATA#" + owner + "#" + repo;
        ProjectMetadataDynamoDbItem item = new ProjectMetadataDynamoDbItem();
        item.setPk(pk);
        item.setSk(SK);
        item.setRole(metadata.getRole());
        item.setYear(metadata.getYear());
        item.setKind(metadata.getKind());
        item.setMainBranch(metadata.getMainBranch());
        item.setStack(metadata.getStack());
        table.putItem(item);
    }
}
