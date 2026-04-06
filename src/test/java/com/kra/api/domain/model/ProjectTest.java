package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private Project buildProject(String idValue) {
        return new Project(
            ProjectId.of(idValue),
            "Test Title",
            "Test description",
            "https://example.com",
            "Test content"
        );
    }

    @Test
    void constructor_allFields_gettersReturnCorrectValues() {
        ProjectId id = ProjectId.of("project-1");
        Project project = new Project(id, "My Title", "My desc", "https://url.com", "My content");

        assertEquals(id, project.getId());
        assertEquals("My Title", project.getTitle());
        assertEquals("My desc", project.getDescription());
        assertEquals("https://url.com", project.getUrl());
        assertEquals("My content", project.getContent());
    }

    @Test
    void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new Project(null, "Title", "Desc", "http://url", "Content"));
    }

    @Test
    void constructor_nullTitle_throwsNullPointerException() {
        ProjectId id = ProjectId.of("project-2");
        assertThrows(NullPointerException.class,
            () -> new Project(id, null, "Desc", "http://url", "Content"));
    }

    @Test
    void constructor_nullDescriptionAndUrl_allowed() {
        ProjectId id = ProjectId.of("project-3");
        Project project = new Project(id, "Title", null, null, null);
        assertNull(project.getDescription());
        assertNull(project.getUrl());
        assertNull(project.getContent());
    }

    @Test
    void equals_sameId_returnsTrue() {
        Project p1 = buildProject("same-id");
        Project p2 = new Project(ProjectId.of("same-id"), "Different Title", "Other desc", null, null);
        assertEquals(p1, p2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Project p1 = buildProject("id-alpha");
        Project p2 = buildProject("id-beta");
        assertNotEquals(p1, p2);
    }
}
