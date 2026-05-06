package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    @Test
    void constructor_allValidFields_gettersReturnCorrectValues() {
        Instant created = Instant.parse("2026-01-01T12:00:00Z");
        Lead lead = new Lead("lead-123", "user@example.com", "Hello, I'm interested!", created);

        assertEquals("lead-123", lead.getId());
        assertEquals("user@example.com", lead.getEmail());
        assertEquals("Hello, I'm interested!", lead.getMessage());
        assertEquals(created, lead.getCreatedAt());
    }

    @Test
    void constructor_nullId_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead(null, "email@test.com", "Message", now));
    }

    @Test
    void constructor_emptyId_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("", "email@test.com", "Message", now));
    }

    @Test
    void constructor_blankId_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("   ", "email@test.com", "Message", now));
    }

    @Test
    void constructor_nullEmail_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", null, "Message", now));
    }

    @Test
    void constructor_emptyEmail_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "", "Message", now));
    }

    @Test
    void constructor_blankEmail_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "   ", "Message", now));
    }

    @Test
    void constructor_nullMessage_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", null, now));
    }

    @Test
    void constructor_emptyMessage_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", "", now));
    }

    @Test
    void constructor_blankMessage_throwsIllegalArgumentException() {
        var now = Instant.now();
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", "   ", now));
    }

    @Test
    void constructor_nullCreatedAt_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new Lead("id-1", "email@test.com", "Message", null));
    }
}
