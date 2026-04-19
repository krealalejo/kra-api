package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceTest {

    @Test
    void constructor_validArgs_storesLabelAndUrl() {
        Reference ref = new Reference("MDN Web Docs", "https://developer.mozilla.org");
        assertEquals("MDN Web Docs", ref.label());
        assertEquals("https://developer.mozilla.org", ref.url());
    }

    @Test
    void constructor_nullLabel_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new Reference(null, "https://example.com"));
    }

    @Test
    void constructor_nullUrl_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new Reference("Label", null));
    }

    @Test
    void recordEquality_sameValues_areEqual() {
        Reference a = new Reference("MDN", "https://developer.mozilla.org");
        Reference b = new Reference("MDN", "https://developer.mozilla.org");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void recordEquality_differentValues_areNotEqual() {
        Reference a = new Reference("MDN", "https://developer.mozilla.org");
        Reference b = new Reference("Other", "https://other.com");
        assertNotEquals(a, b);
    }
}
