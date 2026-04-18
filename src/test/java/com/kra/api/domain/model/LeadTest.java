package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    // Constructor success test
    @Test
    void constructor_allValidFields_gettersReturnCorrectValues() {
        Instant created = Instant.parse("2026-01-01T12:00:00Z");
        Lead lead = new Lead("lead-123", "user@example.com", "Hello, I'm interested!", created);

        assertEquals("lead-123", lead.getId());
        assertEquals("user@example.com", lead.getEmail());
        assertEquals("Hello, I'm interested!", lead.getMessage());
        assertEquals(created, lead.getCreatedAt());
    }

    // ID validation tests
    @Test
    void constructor_nullId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead(null, "email@test.com", "Message", Instant.now()));
    }

    @Test
    void constructor_emptyId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("", "email@test.com", "Message", Instant.now()));
    }

    @Test
    void constructor_blankId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("   ", "email@test.com", "Message", Instant.now()));
    }

    // Email validation tests
    @Test
    void constructor_nullEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", null, "Message", Instant.now()));
    }

    @Test
    void constructor_emptyEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "", "Message", Instant.now()));
    }

    @Test
    void constructor_blankEmail_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "   ", "Message", Instant.now()));
    }

    // Message validation tests
    @Test
    void constructor_nullMessage_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", null, Instant.now()));
    }

    @Test
    void constructor_emptyMessage_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", "", Instant.now()));
    }

    @Test
    void constructor_blankMessage_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lead("id-1", "email@test.com", "   ", Instant.now()));
    }

    // CreatedAt validation test
    @Test
    void constructor_nullCreatedAt_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new Lead("id-1", "email@test.com", "Message", null));
    }
}
