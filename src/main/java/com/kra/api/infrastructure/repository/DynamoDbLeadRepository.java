package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Lead;
import com.kra.api.domain.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DynamoDbLeadRepository implements LeadRepository {

    private final DynamoDbTable<LeadDynamoDbItem> table;

    public DynamoDbLeadRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(LeadDynamoDbItem.class));
    }

    @Override
    public void save(Lead lead) {
        table.putItem(LeadDynamoDbItem.fromDomain(lead));
    }
}
