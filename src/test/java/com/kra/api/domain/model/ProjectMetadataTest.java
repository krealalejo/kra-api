package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProjectMetadataTest {

    @Test
    void testAccessors() {
        List<String> stack = List.of("Java", "Spring");
        ProjectMetadata m = new ProjectMetadata("Role", "2024", "Backend", "main", stack);

        assertEquals("Role", m.role());
        assertEquals("2024", m.year());
        assertEquals("Backend", m.kind());
        assertEquals("main", m.mainBranch());
        assertEquals(stack, m.stack());
    }

    @Test
    void testEquality() {
        List<String> stack = List.of("Vue", "Nuxt");
        ProjectMetadata a = new ProjectMetadata("Lead", "2025", "Frontend", "develop", stack);
        ProjectMetadata b = new ProjectMetadata("Lead", "2025", "Frontend", "develop", stack);

        assertEquals(a, b);
    }
}
