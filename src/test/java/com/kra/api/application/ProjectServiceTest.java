package com.kra.api.application;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.domain.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private final ProjectRepository repository = mock(ProjectRepository.class);
    private final ProjectService service = new ProjectService(repository);

    @BeforeEach
    void resetMocks() {
        reset(repository);
    }

    @Test
    void createProject_savesAndReturnsProject() {
        Project created = service.createProject("My Title", "Desc", "https://url.com", "Content");
        verify(repository).save(any(Project.class));
        assertEquals("My Title", created.getTitle());
        assertNotNull(created.getId().getValue());
    }

    @Test
    void createProject_setsAllFields() {
        Project created = service.createProject("T", "Desc", "https://url.com", "Content");
        assertEquals("Desc", created.getDescription());
        assertEquals("https://url.com", created.getUrl());
        assertEquals("Content", created.getContent());
    }

    @Test
    void getAllProjects_returnsLimitedList() {
        List<Project> fiveProjects = List.of(
                new Project(ProjectId.of("1"), "T1", null, null, null),
                new Project(ProjectId.of("2"), "T2", null, null, null),
                new Project(ProjectId.of("3"), "T3", null, null, null),
                new Project(ProjectId.of("4"), "T4", null, null, null),
                new Project(ProjectId.of("5"), "T5", null, null, null)
        );
        when(repository.findAll()).thenReturn(fiveProjects);
        List<Project> result = service.getAllProjects(3);
        assertEquals(3, result.size());
    }

    @Test
    void getAllProjects_limitLargerThanList() {
        List<Project> twoProjects = List.of(
                new Project(ProjectId.of("1"), "T1", null, null, null),
                new Project(ProjectId.of("2"), "T2", null, null, null)
        );
        when(repository.findAll()).thenReturn(twoProjects);
        List<Project> result = service.getAllProjects(10);
        assertEquals(2, result.size());
    }

    @Test
    void getProjectById_found_returnsOptional() {
        Project project = new Project(ProjectId.of("abc"), "T", null, null, null);
        when(repository.findById(ProjectId.of("abc"))).thenReturn(Optional.of(project));
        Optional<Project> result = service.getProjectById("abc");
        assertTrue(result.isPresent());
        assertEquals("abc", result.get().getId().getValue());
    }

    @Test
    void getProjectById_notFound_returnsEmpty() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        Optional<Project> result = service.getProjectById("missing");
        assertTrue(result.isEmpty());
    }

    @Test
    void updateProject_found_updatesAndReturns() {
        Project project = new Project(ProjectId.of("abc"), "Old", null, null, null);
        when(repository.findById(ProjectId.of("abc"))).thenReturn(Optional.of(project));
        Project updated = service.updateProject("abc", "New Title", "New Desc", "https://new.com", "New Content");
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Desc", updated.getDescription());
        verify(repository).save(project);
    }

    @Test
    void updateProject_notFound_throwsProjectNotFoundException() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class,
                () -> service.updateProject("bad-id", "T", "D", "U", "C"));
    }

    @Test
    void deleteProject_found_deletesSuccessfully() {
        Project project = new Project(ProjectId.of("abc"), "T", null, null, null);
        when(repository.findById(ProjectId.of("abc"))).thenReturn(Optional.of(project));
        assertDoesNotThrow(() -> service.deleteProject("abc"));
        verify(repository).deleteById(ProjectId.of("abc"));
    }

    @Test
    void deleteProject_notFound_throwsProjectNotFoundException() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class,
                () -> service.deleteProject("missing"));
    }
}
