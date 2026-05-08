package com.kra.api.domain;

import com.kra.api.domain.model.SkillCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillCategoryTest extends AbstractIdentifiableEntityTest {

    @Override
    SkillCategory build(String id) {
        return new SkillCategory(id, "Backend", List.of("Java", "Spring"), 1);
    }

    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        SkillCategory cat = new SkillCategory("id-1", "Backend", List.of("Java", "Spring"), 2);
        assertEquals("id-1", cat.getId());
        assertEquals("Backend", cat.getName());
        assertEquals(List.of("Java", "Spring"), cat.getSkills());
        assertEquals(2, cat.getSortOrder());
    }

    @Test
    void constructor_nullId_throws() {
        List<String> emptySkills = List.of();
        assertThrows(NullPointerException.class,
                () -> new SkillCategory(null, "Backend", emptySkills, 0));
    }

    @Test
    void constructor_nullName_throws() {
        List<String> emptyList = List.of();
        assertThrows(NullPointerException.class,
                () -> new SkillCategory("id", null, emptyList, 0));
    }

    @Test
    void constructor_nullSkills_defaultsToEmpty() {
        SkillCategory cat = new SkillCategory("id", "Backend", null, 0);
        assertNotNull(cat.getSkills());
        assertTrue(cat.getSkills().isEmpty());
    }

    @Test
    void setters_updateFields() {
        SkillCategory cat = build("1");
        cat.setName("Frontend");
        cat.setSkills(List.of("Vue", "TypeScript"));
        cat.setSortOrder(5);

        assertEquals("Frontend", cat.getName());
        assertEquals(List.of("Vue", "TypeScript"), cat.getSkills());
        assertEquals(5, cat.getSortOrder());
    }

    @Test
    void setName_null_throws() {
        SkillCategory cat = build("1");
        assertThrows(NullPointerException.class, () -> cat.setName(null));
    }

    @Test
    void setSkills_null_defaultsToEmpty() {
        SkillCategory cat = build("1");
        cat.setSkills(null);
        assertNotNull(cat.getSkills());
        assertTrue(cat.getSkills().isEmpty());
    }

    @Test
    void skills_returnedListIsUnmodifiable() {
        SkillCategory cat = build("1");
        var catSkills = cat.getSkills();
        assertThrows(UnsupportedOperationException.class, () -> catSkills.add("extra"));
    }

    @Test
    void equals_sameId_returnsTrue() {
        SkillCategory c1 = build("same");
        SkillCategory c2 = new SkillCategory("same", "Other", List.of(), 99);
        assertEquals(c1, c2);
    }

    @Test
    void equals_withNull_returnsFalse() {
        SkillCategory c = build("1");
        assertFalse(c.equals(null));
    }

}
