package com.kra.api.domain;

import com.kra.api.domain.model.Experience;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceTest {

    private Experience build(String id) {
        return new Experience(id, "Engineer", "Acme", "London", "2020-2023", "Built things", 1);
    }

    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        Experience exp = new Experience("id-1", "Engineer", "Acme", "London", "2020", "desc", 2);
        assertEquals("id-1", exp.getId());
        assertEquals("Engineer", exp.getTitle());
        assertEquals("Acme", exp.getCompany());
        assertEquals("London", exp.getLocation());
        assertEquals("2020", exp.getYears());
        assertEquals("desc", exp.getDescription());
        assertEquals(2, exp.getSortOrder());
    }

    @Test
    void constructor_nullId_throws() {
        assertThrows(NullPointerException.class,
                () -> new Experience(null, "T", "C", "L", "Y", "D", 0));
    }

    @Test
    void constructor_nullTitle_throws() {
        assertThrows(NullPointerException.class,
                () -> new Experience("id", null, "C", "L", "Y", "D", 0));
    }

    @Test
    void constructor_nullOptionalFields_allowed() {
        Experience exp = new Experience("id", "T", null, null, null, null, 0);
        assertNull(exp.getCompany());
        assertNull(exp.getLocation());
        assertNull(exp.getYears());
        assertNull(exp.getDescription());
    }

    @Test
    void setters_updateFields() {
        Experience exp = build("1");
        exp.setTitle("New Title");
        exp.setCompany("New Corp");
        exp.setLocation("NYC");
        exp.setYears("2024");
        exp.setDescription("New desc");
        exp.setSortOrder(5);

        assertEquals("New Title", exp.getTitle());
        assertEquals("New Corp", exp.getCompany());
        assertEquals("NYC", exp.getLocation());
        assertEquals("2024", exp.getYears());
        assertEquals("New desc", exp.getDescription());
        assertEquals(5, exp.getSortOrder());
    }

    @Test
    void setTitle_null_throws() {
        Experience exp = build("1");
        assertThrows(NullPointerException.class, () -> exp.setTitle(null));
    }

    @Test
    void equals_sameId_returnsTrue() {
        Experience e1 = build("same");
        Experience e2 = new Experience("same", "Other", "Other", "Other", "Other", "Other", 99);
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

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, build("1"));
    }

    @Test
    void equals_sameObject_returnsTrue() {
        Experience e = build("1");
        assertEquals(e, e);
    }
}
