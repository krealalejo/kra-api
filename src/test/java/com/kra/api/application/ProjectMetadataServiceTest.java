package com.kra.api.application;

import com.kra.api.domain.model.ProjectMetadata;
import com.kra.api.domain.repository.ProjectMetadataRepository;
import com.kra.api.infrastructure.web.dto.ProjectMetadataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProjectMetadataServiceTest {

    @Mock
    private ProjectMetadataRepository repository;

    @InjectMocks
    private ProjectMetadataService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMetadata() {
        ProjectMetadata m = new ProjectMetadata("Role", "2024", "Backend", "main", List.of("Java"));
        when(repository.findByOwnerAndRepo("owner", "repo")).thenReturn(m);

        ProjectMetadataResponse response = service.getMetadata("owner", "repo");

        assertEquals("Role", response.role());
        assertEquals("2024", response.year());
        assertEquals("Backend", response.kind());
        assertEquals("main", response.mainBranch());
        assertEquals(List.of("Java"), response.stack());
    }

    @Test
    void testUpsertMetadata() {
        ProjectMetadata m = new ProjectMetadata();
        when(repository.findByOwnerAndRepo("owner", "repo")).thenReturn(m);

        List<String> stack = List.of("Vue");
        ProjectMetadataResponse response = service.upsertMetadata("owner", "repo", "Lead", "2025", "Frontend", "develop", stack);

        assertEquals("Lead", response.role());
        assertEquals("2025", response.year());
        assertEquals("Frontend", response.kind());
        assertEquals("develop", response.mainBranch());
        assertEquals(stack, response.stack());

        verify(repository).save(eq("owner"), eq("repo"), any(ProjectMetadata.class));
    }
}
