package com.kra.api.domain;

import com.kra.api.domain.model.Education;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EducationTest {

    private Education build(String id) {
        return new Education(id, "BSc CS", "MIT", "Cambridge", "2015-2019", "Graduated top of class", 1);
    }

    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        Education edu = new Education("id-1", "BSc CS", "MIT", "Cambridge", "2015-2019", "desc", 2);
        assertEquals("id-1", edu.getId());
        assertEquals("BSc CS", edu.getTitle());
        assertEquals("MIT", edu.getInstitution());
        assertEquals("Cambridge", edu.getLocation());
        assertEquals("2015-2019", edu.getYears());
        assertEquals("desc", edu.getDescription());
        assertEquals(2, edu.getSortOrder());
    }

    @Test
    void constructor_nullId_throws() {
        assertThrows(NullPointerException.class,
                () -> new Education(null, "T", "I", "L", "Y", "D", 0));
    }

    @Test
    void constructor_nullTitle_throws() {
        assertThrows(NullPointerException.class,
                () -> new Education("id", null, "I", "L", "Y", "D", 0));
    }

    @Test
    void constructor_nullOptionalFields_allowed() {
        Education edu = new Education("id", "T", null, null, null, null, 0);
        assertNull(edu.getInstitution());
        assertNull(edu.getLocation());
        assertNull(edu.getYears());
        assertNull(edu.getDescription());
    }

    @Test
    void setters_updateFields() {
        Education edu = build("1");
        edu.setTitle("New Title");
        edu.setInstitution("Oxford");
        edu.setLocation("UK");
        edu.setYears("2020");
        edu.setDescription("New desc");
        edu.setSortOrder(5);

        assertEquals("New Title", edu.getTitle());
        assertEquals("Oxford", edu.getInstitution());
        assertEquals("UK", edu.getLocation());
        assertEquals("2020", edu.getYears());
        assertEquals("New desc", edu.getDescription());
        assertEquals(5, edu.getSortOrder());
    }

    @Test
    void setTitle_null_throws() {
        Education edu = build("1");
        assertThrows(NullPointerException.class, () -> edu.setTitle(null));
    }

    @Test
    void equals_sameId_returnsTrue() {
        Education e1 = build("same");
        Education e2 = new Education("same", "Other", "Other", "Other", "Other", "Other", 99);
        assertEquals(e1, e2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        assertNotEquals(build("a"), build("b"));
    }

    @Test
    void hashCode_sameId_equal() {
        assertEquals(build("x").hashCode(), build("x").hashCode());
    }

    @Test
    void equals_differentType_returnsFalse() {
        assertNotEquals("string", build("1"));
    }
}
