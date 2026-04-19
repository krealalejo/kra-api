package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlogPostTest {

    private BlogPost buildPost(String slugValue) {
        return new BlogPost(
            BlogSlug.of(slugValue),
            "Test Title",
            "Test content",
            Instant.now(),
            Instant.now(),
            List.of()
        );
    }

    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        BlogSlug slug = BlogSlug.of("my-post");
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        Instant updated = Instant.parse("2026-01-02T00:00:00Z");
        BlogPost post = new BlogPost(slug, "My Title", "My content", created, updated, List.of());

        assertEquals(slug, post.getSlug());
        assertEquals("My Title", post.getTitle());
        assertEquals("My content", post.getContent());
        assertEquals(created, post.getCreatedAt());
        assertEquals(updated, post.getUpdatedAt());
    }

    @Test
    void constructor_nullSlug_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new BlogPost(null, "Title", "Content", Instant.now(), Instant.now(), List.of()));
    }

    @Test
    void constructor_nullTitle_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, null, "Content", Instant.now(), Instant.now(), List.of()));
    }

    @Test
    void constructor_nullContent_defaultsToEmptyString() {
        BlogSlug slug = BlogSlug.of("test");
        BlogPost post = new BlogPost(slug, "Title", null, Instant.now(), Instant.now(), List.of());
        assertEquals("", post.getContent());
    }

    @Test
    void constructor_nullCreatedAt_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, "Title", "Content", null, Instant.now(), List.of()));
    }

    @Test
    void constructor_nullUpdatedAt_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, "Title", "Content", Instant.now(), null, List.of()));
    }

    @Test
    void constructor_nullReferences_defaultsToEmptyList() {
        BlogSlug slug = BlogSlug.of("test");
        BlogPost post = new BlogPost(slug, "Title", "Content", Instant.now(), Instant.now(), null);
        assertNotNull(post.getReferences());
        assertTrue(post.getReferences().isEmpty());
    }

    @Test
    void constructor_withReferences_returnsImmutableList() {
        BlogSlug slug = BlogSlug.of("test");
        Reference ref = new Reference("MDN", "https://developer.mozilla.org");
        BlogPost post = new BlogPost(slug, "Title", "Content", Instant.now(), Instant.now(), List.of(ref));
        assertEquals(1, post.getReferences().size());
        assertEquals("MDN", post.getReferences().get(0).label());
        assertThrows(UnsupportedOperationException.class,
            () -> post.getReferences().add(new Reference("x", "https://x.com")));
    }

    @Test
    void setTitle_validValue_updatesTitle() {
        BlogPost post = buildPost("test");
        post.setTitle("New Title");
        assertEquals("New Title", post.getTitle());
    }

    @Test
    void setTitle_nullValue_throwsNullPointerException() {
        BlogPost post = buildPost("test");
        assertThrows(NullPointerException.class, () -> post.setTitle(null));
    }

    @Test
    void setContent_validValue_updatesContent() {
        BlogPost post = buildPost("test");
        post.setContent("New content");
        assertEquals("New content", post.getContent());
    }

    @Test
    void setContent_nullValue_defaultsToEmptyString() {
        BlogPost post = buildPost("test");
        post.setContent(null);
        assertEquals("", post.getContent());
    }

    @Test
    void touchUpdatedAt_validValue_updatesTimestamp() {
        BlogPost post = buildPost("test");
        Instant newTime = Instant.parse("2099-12-31T23:59:59Z");
        post.touchUpdatedAt(newTime);
        assertEquals(newTime, post.getUpdatedAt());
    }

    @Test
    void touchUpdatedAt_nullValue_throwsNullPointerException() {
        BlogPost post = buildPost("test");
        assertThrows(NullPointerException.class, () -> post.touchUpdatedAt(null));
    }

    @Test
    void setReferences_nullValue_defaultsToEmptyList() {
        BlogPost post = buildPost("test");
        post.setReferences(null);
        assertNotNull(post.getReferences());
        assertTrue(post.getReferences().isEmpty());
    }

    @Test
    void setReferences_validList_updatesReferences() {
        BlogPost post = buildPost("test");
        Reference ref = new Reference("MDN", "https://developer.mozilla.org");
        post.setReferences(List.of(ref));
        assertEquals(1, post.getReferences().size());
        assertEquals("MDN", post.getReferences().get(0).label());
    }
}
