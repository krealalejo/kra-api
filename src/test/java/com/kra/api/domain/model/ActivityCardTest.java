package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActivityCardTest {

    @Test
    void testConstructorAndGetters() {
        ActivityCard card = new ActivityCard("SHIPPING", "Title", "Desc", List.of("tag"));
        assertEquals("SHIPPING", card.getType());
        assertEquals("Title", card.getTitle());
        assertEquals("Desc", card.getDescription());
        assertEquals(List.of("tag"), card.getTags());
    }

    @Test
    void testSetters() {
        ActivityCard card = new ActivityCard();
        card.setType("LEARNING");
        card.setTitle("New Title");
        card.setDescription("New Desc");
        card.setTags(List.of("new"));

        assertEquals("LEARNING", card.getType());
        assertEquals("New Title", card.getTitle());
        assertEquals("New Desc", card.getDescription());
        assertEquals(List.of("new"), card.getTags());
    }
}
