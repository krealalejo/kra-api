package com.kra.api.application;

import com.kra.api.domain.model.Lead;
import com.kra.api.domain.repository.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    private final LeadRepository repository = mock(LeadRepository.class);
    private final ContactService service = new ContactService(repository);

    @BeforeEach
    void resetMocks() {
        reset(repository);
    }

    @Test
    void submitLead_savesLeadAndReturnsId() {
        String id = service.submitLead("user@example.com", "Hello, I'm interested!");
        assertNotNull(id);
        assertFalse(id.isBlank());
        verify(repository).save(any(Lead.class));
    }

    @Test
    void submitLead_generatesUniqueIds() {
        String id1 = service.submitLead("a@b.com", "Message 1");
        String id2 = service.submitLead("c@d.com", "Message 2");
        assertNotEquals(id1, id2);
    }

    @Test
    void submitLead_passesCorrectDataToRepository() {
        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        service.submitLead("test@example.com", "My message");
        verify(repository).save(captor.capture());
        Lead saved = captor.getValue();
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("My message", saved.getMessage());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void submitLead_returnsIdMatchingSavedLead() {
        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        String returnedId = service.submitLead("test@example.com", "Message");
        verify(repository).save(captor.capture());
        assertEquals(returnedId, captor.getValue().getId());
    }
}
