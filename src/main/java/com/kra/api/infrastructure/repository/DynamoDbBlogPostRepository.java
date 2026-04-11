package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class DynamoDbBlogPostRepository implements BlogPostRepository {

    private static final String GSI1_NAME = "GSI1";
    private static final String TYPE_POST = "TYPE#POST";

    private final DynamoDbTable<PostDynamoDbItem> table;

    public DynamoDbBlogPostRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(PostDynamoDbItem.class));
    }

    @Override
    public void save(BlogPost post) {
        table.putItem(PostDynamoDbItem.fromDomain(post));
    }

    @Override
    public Optional<BlogPost> findBySlug(BlogSlug slug) {
        Key key = Key.builder()
                .partitionValue("POST#" + slug.getValue())
                .sortValue("METADATA")
                .build();
        PostDynamoDbItem item = table.getItem(key);
        return Optional.ofNullable(item).map(PostDynamoDbItem::toDomain);
    }

    @Override
    public List<BlogPost> findAllByNewestFirst() {
        DynamoDbIndex<PostDynamoDbItem> gsi1 = table.index(GSI1_NAME);
        QueryConditional condition = QueryConditional.keyEqualTo(k -> k.partitionValue(TYPE_POST));
        List<BlogPost> posts = StreamSupport.stream(gsi1.query(condition).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(item -> "METADATA".equals(item.getSk()))
                .map(PostDynamoDbItem::toDomain)
                .sorted(Comparator.comparing(BlogPost::getCreatedAt).reversed())
                .toList();
        return posts;
    }

    @Override
    public void deleteBySlug(BlogSlug slug) {
        Key key = Key.builder()
                .partitionValue("POST#" + slug.getValue())
                .sortValue("METADATA")
                .build();
        table.deleteItem(key);
    }
}
