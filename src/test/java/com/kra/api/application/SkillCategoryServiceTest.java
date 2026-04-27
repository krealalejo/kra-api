package com.kra.api.application;

import com.kra.api.domain.model.SkillCategory;
import com.kra.api.domain.repository.SkillCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillCategoryServiceTest {

    @Mock
    private SkillCategoryRepository repository;

    @InjectMocks
    private SkillCategoryService service;

    @Test
    void create_shouldSaveAndReturnCategory() {
        SkillCategory result = service.create("Name", List.of("Java"), 1);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Name", result.getName());
        verify(repository).save(any(SkillCategory.class));
    }

    @Test
    void findAll_shouldReturnList() {
        List<SkillCategory> list = List.of(new SkillCategory("1", "N", List.of("S"), 1));
        when(repository.findAll()).thenReturn(list);

        List<SkillCategory> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void update_shouldUpdateAllFields() {
        SkillCategory existing = new SkillCategory("1", "Old N", List.of("Old S"), 0);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        SkillCategory result = service.update("1", "New N", List.of("New S"), 10);

        assertEquals("New N", result.getName());
        assertEquals(List.of("New S"), result.getSkills());
        assertEquals(10, result.getSortOrder());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldNotUpdateNullFields() {
        SkillCategory existing = new SkillCategory("1", "Old N", List.of("Old S"), 5);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        SkillCategory result = service.update("1", null, null, null);

        assertEquals("Old N", result.getName());
        assertEquals(List.of("Old S"), result.getSkills());
        assertEquals(5, result.getSortOrder());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(SkillCategoryNotFoundException.class, () ->
            service.update("1", "N", List.of("S"), 1));
    }

    @Test
    void delete_shouldDeleteIfFound() {
        SkillCategory existing = new SkillCategory("1", "N", List.of("S"), 1);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        service.delete("1");

        verify(repository).deleteById("1");
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(SkillCategoryNotFoundException.class, () -> service.delete("1"));
    }
}
