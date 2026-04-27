package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.AppConfig;
import com.kra.api.domain.repository.AppConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DynamoDbAppConfigRepository implements AppConfigRepository {

    private static final String PK = "CONFIG#profile";
    private static final String SK = "METADATA";

    private final DynamoDbTable<AppConfigDynamoDbItem> table;

    public DynamoDbAppConfigRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(AppConfigDynamoDbItem.class));
    }

    @Override
    public AppConfig findProfile() {
        Key key = Key.builder().partitionValue(PK).sortValue(SK).build();
        AppConfigDynamoDbItem item = table.getItem(key);
        if (item == null) {
            return new AppConfig(null, null, null);
        }
        return new AppConfig(item.getHomePortraitUrl(), item.getCvPortraitUrl(), item.getCvPdfUrl());
    }

    @Override
    public void saveProfile(AppConfig config) {
        AppConfigDynamoDbItem item = new AppConfigDynamoDbItem();
        item.setPk(PK);
        item.setSk(SK);
        item.setHomePortraitUrl(config.getHomePortraitUrl());
        item.setCvPortraitUrl(config.getCvPortraitUrl());
        item.setCvPdfUrl(config.getCvPdfUrl());
        table.putItem(item);
    }
}
