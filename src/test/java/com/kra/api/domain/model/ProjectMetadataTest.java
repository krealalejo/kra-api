package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProjectMetadataTest {

    @Test
    void testConstructorAndGetters() {
        List<String> stack = List.of("Java", "Spring");
        ProjectMetadata m = new ProjectMetadata("Role", "2024", "Backend", "main", stack);

        assertEquals("Role", m.getRole());
        assertEquals("2024", m.getYear());
        assertEquals("Backend", m.getKind());
        assertEquals("main", m.getMainBranch());
        assertEquals(stack, m.getStack());
    }

    @Test
    void testSetters() {
        ProjectMetadata m = new ProjectMetadata();
        List<String> stack = List.of("Vue", "Nuxt");

        m.setRole("Lead");
        m.setYear("2025");
        m.setKind("Frontend");
        m.setMainBranch("develop");
        m.setStack(stack);

        assertEquals("Lead", m.getRole());
        assertEquals("2025", m.getYear());
        assertEquals("Frontend", m.getKind());
        assertEquals("develop", m.getMainBranch());
        assertEquals(stack, m.getStack());
    }
}
