package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActivityCardTest {

    @Test
    void testAccessors() {
        ActivityCard card = new ActivityCard("SHIPPING", "Title", "Desc", List.of("tag"), "https://example.com");
        assertEquals("SHIPPING", card.type());
        assertEquals("Title", card.title());
        assertEquals("Desc", card.description());
        assertEquals(List.of("tag"), card.tags());
        assertEquals("https://example.com", card.url());
    }

    @Test
    void testEquality() {
        ActivityCard a = new ActivityCard("LEARNING", "New Title", "New Desc", List.of("new"), "https://x.dev");
        ActivityCard b = new ActivityCard("LEARNING", "New Title", "New Desc", List.of("new"), "https://x.dev");

        assertEquals(a, b);
    }
}
