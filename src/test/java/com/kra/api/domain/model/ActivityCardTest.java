package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActivityCardTest {

    @Test
    void testAccessors() {
        ActivityCard card = new ActivityCard("SHIPPING", "Title", "Desc", List.of("tag"));
        assertEquals("SHIPPING", card.type());
        assertEquals("Title", card.title());
        assertEquals("Desc", card.description());
        assertEquals(List.of("tag"), card.tags());
    }

    @Test
    void testEquality() {
        ActivityCard a = new ActivityCard("LEARNING", "New Title", "New Desc", List.of("new"));
        ActivityCard b = new ActivityCard("LEARNING", "New Title", "New Desc", List.of("new"));

        assertEquals(a, b);
    }
}
