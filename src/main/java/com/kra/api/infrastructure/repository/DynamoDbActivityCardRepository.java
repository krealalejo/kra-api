package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.ActivityCard;
import com.kra.api.domain.repository.ActivityCardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDbActivityCardRepository implements ActivityCardRepository {

    private static final String PK = "CONFIG";
    private static final String SK_SHIPPING = "ACTIVITY#SHIPPING";
    private static final String SK_READING  = "ACTIVITY#READING";
    private static final String SK_PLAYING  = "ACTIVITY#PLAYING";

    private final DynamoDbTable<ActivityCardDynamoDbItem> table;

    public DynamoDbActivityCardRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name:kra-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(ActivityCardDynamoDbItem.class));
    }

    @Override
    public List<ActivityCard> findAll() {
        List<ActivityCard> cards = new ArrayList<>();
        for (String sk : List.of(SK_SHIPPING, SK_READING, SK_PLAYING)) {
            Key key = Key.builder().partitionValue(PK).sortValue(sk).build();
            ActivityCardDynamoDbItem item = table.getItem(key);
            String type = sk.substring("ACTIVITY#".length());
            if (item == null) {
                cards.add(new ActivityCard(type, null, null, null));
            } else {
                cards.add(new ActivityCard(type, item.getTitle(), item.getDescription(), item.getTags()));
            }
        }
        return cards;
    }

    @Override
    public Optional<ActivityCard> findByType(String type) {
        String sk = "ACTIVITY#" + type.toUpperCase();
        Key key = Key.builder().partitionValue(PK).sortValue(sk).build();
        ActivityCardDynamoDbItem item = table.getItem(key);
        if (item == null) return Optional.empty();
        return Optional.of(new ActivityCard(type.toUpperCase(), item.getTitle(), item.getDescription(), item.getTags()));
    }

    @Override
    public void save(ActivityCard card) {
        String sk = "ACTIVITY#" + card.getType();
        ActivityCardDynamoDbItem item = new ActivityCardDynamoDbItem();
        item.setPk(PK);
        item.setSk(sk);
        item.setTitle(card.getTitle());
        item.setDescription(card.getDescription());
        item.setTags(card.getTags());
        table.putItem(item);
    }
}
