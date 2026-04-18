package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BlogPostTest {

    private BlogPost buildPost(String slugValue) {
        return new BlogPost(
            BlogSlug.of(slugValue),
            "Test Title",
            "Test content",
            Instant.now(),
            Instant.now()
        );
    }

    // Constructor tests
    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        BlogSlug slug = BlogSlug.of("my-post");
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        Instant updated = Instant.parse("2026-01-02T00:00:00Z");
        BlogPost post = new BlogPost(slug, "My Title", "My content", created, updated);

        assertEquals(slug, post.getSlug());
        assertEquals("My Title", post.getTitle());
        assertEquals("My content", post.getContent());
        assertEquals(created, post.getCreatedAt());
        assertEquals(updated, post.getUpdatedAt());
    }

    @Test
    void constructor_nullSlug_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new BlogPost(null, "Title", "Content", Instant.now(), Instant.now()));
    }

    @Test
    void constructor_nullTitle_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, null, "Content", Instant.now(), Instant.now()));
    }

    @Test
    void constructor_nullContent_defaultsToEmptyString() {
        BlogSlug slug = BlogSlug.of("test");
        BlogPost post = new BlogPost(slug, "Title", null, Instant.now(), Instant.now());
        assertEquals("", post.getContent());
    }

    @Test
    void constructor_nullCreatedAt_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, "Title", "Content", null, Instant.now()));
    }

    @Test
    void constructor_nullUpdatedAt_throwsNullPointerException() {
        BlogSlug slug = BlogSlug.of("test");
        assertThrows(NullPointerException.class,
            () -> new BlogPost(slug, "Title", "Content", Instant.now(), null));
    }

    // Setter tests
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
}
