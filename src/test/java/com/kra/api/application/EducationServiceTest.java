package com.kra.api.application;

import com.kra.api.domain.model.Education;
import com.kra.api.domain.repository.EducationRepository;
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
class EducationServiceTest {

    @Mock
    private EducationRepository repository;

    @InjectMocks
    private EducationService service;

    @Test
    void create_shouldSaveAndReturnEducation() {
        Education result = service.create("Title", "Inst", "Loc", "2020", "Desc", 1);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Title", result.getTitle());
        verify(repository).save(any(Education.class));
    }

    @Test
    void findAll_shouldReturnList() {
        List<Education> list = List.of(new Education("1", "T", "I", "L", "Y", "D", 1));
        when(repository.findAll()).thenReturn(list);

        List<Education> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void update_shouldUpdateAllFields() {
        Education existing = new Education("1", "Old T", "Old I", "Old L", "Old Y", "Old D", 0);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        Education result = service.update("1", "New T", "New I", "New L", "New Y", "New D", 10);

        assertEquals("New T", result.getTitle());
        assertEquals("New I", result.getInstitution());
        assertEquals("New L", result.getLocation());
        assertEquals("New Y", result.getYears());
        assertEquals("New D", result.getDescription());
        assertEquals(10, result.getSortOrder());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldNotUpdateNullFields() {
        Education existing = new Education("1", "Old T", "Old I", "Old L", "Old Y", "Old D", 5);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        Education result = service.update("1", null, null, null, null, null, null);

        assertEquals("Old T", result.getTitle());
        assertEquals("Old I", result.getInstitution());
        assertEquals("Old L", result.getLocation());
        assertEquals("Old Y", result.getYears());
        assertEquals("Old D", result.getDescription());
        assertEquals(5, result.getSortOrder());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EducationNotFoundException.class, () -> 
            service.update("1", "T", "I", "L", "Y", "D", 1));
    }

    @Test
    void delete_shouldDeleteIfFound() {
        Education existing = new Education("1", "T", "I", "L", "Y", "D", 1);
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        service.delete("1");

        verify(repository).deleteById("1");
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EducationNotFoundException.class, () -> service.delete("1"));
    }
}
