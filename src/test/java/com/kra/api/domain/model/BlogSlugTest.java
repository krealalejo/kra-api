package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlogSlugTest {

    // Factory method tests
    @Test
    void of_validSlug_createsInstance() {
        BlogSlug slug = BlogSlug.of("my-post");
        assertEquals("my-post", slug.getValue());
    }

    @Test
    void of_slugWithNumbers_createsInstance() {
        BlogSlug slug = BlogSlug.of("post-123");
        assertEquals("post-123", slug.getValue());
    }

    @Test
    void of_singleCharacter_createsInstance() {
        BlogSlug slug = BlogSlug.of("a");
        assertEquals("a", slug.getValue());
    }

    @Test
    void of_maxLength128_createsInstance() {
        String longSlug = "a".repeat(128);
        BlogSlug slug = BlogSlug.of(longSlug);
        assertEquals(longSlug, slug.getValue());
    }

    // Validation tests - null/blank
    @Test
    void of_nullValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of(null));
    }

    @Test
    void of_emptyString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of(""));
    }

    @Test
    void of_blankString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of("   "));
    }

    // Validation tests - pattern violations
    @Test
    void of_uppercaseLetters_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of("My-Post"));
    }

    @Test
    void of_containsSpace_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of("my post"));
    }

    @Test
    void of_containsUnderscore_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of("my_post"));
    }

    @Test
    void of_containsSpecialChars_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of("my@post"));
    }

    @Test
    void of_exceedsMaxLength_throwsIllegalArgumentException() {
        String tooLong = "a".repeat(129);
        assertThrows(IllegalArgumentException.class, () -> BlogSlug.of(tooLong));
    }

    // Equality tests
    @Test
    void equals_sameValue_returnsTrue() {
        BlogSlug slug1 = BlogSlug.of("same-slug");
        BlogSlug slug2 = BlogSlug.of("same-slug");
        assertEquals(slug1, slug2);
    }

    @Test
    void equals_differentValue_returnsFalse() {
        BlogSlug slug1 = BlogSlug.of("slug-a");
        BlogSlug slug2 = BlogSlug.of("slug-b");
        assertNotEquals(slug1, slug2);
    }

    @Test
    void equals_null_returnsFalse() {
        BlogSlug slug = BlogSlug.of("test");
        assertNotEquals(null, slug);
    }

    @Test
    void equals_differentType_returnsFalse() {
        BlogSlug slug = BlogSlug.of("test");
        assertNotEquals("test", slug);
    }

    @Test
    void hashCode_sameValue_equalHashCodes() {
        BlogSlug slug1 = BlogSlug.of("same-slug");
        BlogSlug slug2 = BlogSlug.of("same-slug");
        assertEquals(slug1.hashCode(), slug2.hashCode());
    }

    @Test
    void toString_containsValue() {
        BlogSlug slug = BlogSlug.of("my-post");
        assertTrue(slug.toString().contains("my-post"));
    }
}
