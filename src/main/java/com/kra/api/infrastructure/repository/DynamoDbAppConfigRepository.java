package com.kra.api.infrastructure.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DynamoDbAppConfigRepository {

    private static final String PK = "CONFIG#profile";
    private static final String SK = "METADATA";

    private final DynamoDbTable<AppConfigDynamoDbItem> table;

    public DynamoDbAppConfigRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(AppConfigDynamoDbItem.class));
    }

    public AppConfigDynamoDbItem findProfile() {
        Key key = Key.builder().partitionValue(PK).sortValue(SK).build();
        AppConfigDynamoDbItem item = table.getItem(key);
        if (item == null) {
            AppConfigDynamoDbItem empty = new AppConfigDynamoDbItem();
            empty.setPk(PK);
            empty.setSk(SK);
            return empty;
        }
        return item;
    }

    public void saveProfile(AppConfigDynamoDbItem item) {
        item.setPk(PK);
        item.setSk(SK);
        table.putItem(item);
    }
}
