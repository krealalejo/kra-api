package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectIdTest {

    @Test
    void of_validValue_returnsProjectIdWithValue() {
        ProjectId id = ProjectId.of("abc-123");
        assertEquals("abc-123", id.getValue());
    }

    @Test
    void of_nullValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ProjectId.of(null));
    }

    @Test
    void of_emptyValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ProjectId.of(""));
    }

    @Test
    void of_blankValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ProjectId.of("   "));
    }

    @Test
    void equals_sameValue_returnsTrue() {
        ProjectId id1 = ProjectId.of("same-id");
        ProjectId id2 = ProjectId.of("same-id");
        assertEquals(id1, id2);
    }

    @Test
    void equals_differentValue_returnsFalse() {
        ProjectId id1 = ProjectId.of("id-one");
        ProjectId id2 = ProjectId.of("id-two");
        assertNotEquals(id1, id2);
    }

    @Test
    void hashCode_sameValue_returnsSameHash() {
        ProjectId id1 = ProjectId.of("hash-test");
        ProjectId id2 = ProjectId.of("hash-test");
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void equals_differentType_returnsFalse() {
        ProjectId id = ProjectId.of("1");
        assertNotEquals("1", id);
    }

    @Test
    void toString_returnsValue() {
        ProjectId id = ProjectId.of("xyz");
        assertEquals("ProjectId{xyz}", id.toString());
    }
}
