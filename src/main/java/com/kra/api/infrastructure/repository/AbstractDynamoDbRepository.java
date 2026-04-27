package com.kra.api.infrastructure.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    protected List<D> findAllByGsi1(String typeValue, Function<I, D> toDomain,
            Predicate<I> filter, Comparator<D> comparator) {
        DynamoDbIndex<I> gsi1 = table.index("GSI1");
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(typeValue));
        Stream<D> stream = StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(filter)
                .map(toDomain);
        return (comparator != null ? stream.sorted(comparator) : stream).toList();
    }

    protected Key buildKey(String id) {
        return Key.builder()
                .partitionValue(pkPrefix + id)
                .sortValue("METADATA")
                .build();
    }
}
