package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.model.Reference;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostDynamoDbItemTest {

    @Test
    void fromDomain_mapsAllFields() {
        BlogSlug slug = BlogSlug.of("my-post");
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        Instant updated = Instant.parse("2026-01-02T00:00:00Z");
        Reference ref = new Reference("MDN", "https://developer.mozilla.org");
        BlogPost post = new BlogPost(slug, "My Title", "My Content", created, updated, List.of(ref), null);

        PostDynamoDbItem item = PostDynamoDbItem.fromDomain(post);

        assertEquals("POST#my-post", item.getPk());
        assertEquals("METADATA", item.getSk());
        assertEquals("TYPE#POST", item.getGsi1pk());
        assertEquals("My Title", item.getTitle());
        assertEquals("My Content", item.getContent());
        assertEquals(created.toEpochMilli(), item.getCreatedAtMillis());
        assertEquals(updated.toEpochMilli(), item.getUpdatedAtMillis());
        assertEquals(1, item.getReferences().size());
        assertEquals("MDN", item.getReferences().get(0).getLabel());
    }

    @Test
    void toDomain_withAllFieldsPresent_mapsCorrectly() {
        PostDynamoDbItem item = new PostDynamoDbItem();
        item.setPk("POST#my-post");
        item.setSk("METADATA");
        item.setTitle("Title");
        item.setContent("Content");
        item.setCreatedAtMillis(1000L);
        item.setUpdatedAtMillis(2000L);
        item.setReferences(List.of(new ReferenceItem("MDN", "https://developer.mozilla.org")));

        BlogPost post = item.toDomain();

        assertEquals("my-post", post.getSlug().getValue());
        assertEquals("Title", post.getTitle());
        assertEquals("Content", post.getContent());
        assertEquals(Instant.ofEpochMilli(1000L), post.getCreatedAt());
        assertEquals(Instant.ofEpochMilli(2000L), post.getUpdatedAt());
        assertEquals(1, post.getReferences().size());
    }

    @Test
    void toDomain_nullCreatedAtMillis_defaultsToEpochZero() {
        PostDynamoDbItem item = new PostDynamoDbItem();
        item.setPk("POST#slug");
        item.setTitle("T");
        item.setContent("C");
        item.setCreatedAtMillis(null);
        item.setUpdatedAtMillis(null);
        item.setReferences(List.of());

        BlogPost post = item.toDomain();

        assertEquals(Instant.ofEpochMilli(0L), post.getCreatedAt());
        assertEquals(Instant.ofEpochMilli(0L), post.getUpdatedAt());
    }

    @Test
    void toDomain_nullReferences_defaultsToEmptyList() {
        PostDynamoDbItem item = new PostDynamoDbItem();
        item.setPk("POST#slug");
        item.setTitle("T");
        item.setContent("C");
        item.setCreatedAtMillis(1000L);
        item.setUpdatedAtMillis(2000L);
        item.setReferences(null);

        BlogPost post = item.toDomain();

        assertNotNull(post.getReferences());
        assertTrue(post.getReferences().isEmpty());
    }

    @Test
    void toDomain_nullContent_defaultsToEmptyString() {
        PostDynamoDbItem item = new PostDynamoDbItem();
        item.setPk("POST#slug");
        item.setTitle("T");
        item.setContent(null);
        item.setCreatedAtMillis(1000L);
        item.setUpdatedAtMillis(2000L);
        item.setReferences(List.of());

        BlogPost post = item.toDomain();

        assertEquals("", post.getContent());
    }
}
