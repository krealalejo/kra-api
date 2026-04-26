package com.kra.api.infrastructure.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDynamoDbRepository<D, I> {

    protected final DynamoDbTable<I> table;
    protected final String pkPrefix;

    protected AbstractDynamoDbRepository(
            DynamoDbEnhancedClient enhancedClient,
            String tableName,
            Class<I> itemClass,
            String pkPrefix) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(itemClass));
        this.pkPrefix = pkPrefix;
    }

    public void save(D domain, Function<D, I> fromDomain) {
        table.putItem(fromDomain.apply(domain));
    }

    public Optional<D> findById(String id, Function<I, D> toDomain) {
        Key key = buildKey(id);
        I item = table.getItem(key);
        return Optional.ofNullable(item).map(toDomain);
    }

    public void deleteById(String id) {
        table.deleteItem(buildKey(id));
    }

    protected Key buildKey(String id) {
        return Key.builder()
                .partitionValue(pkPrefix + id)
                .sortValue("METADATA")
                .build();
    }
}
