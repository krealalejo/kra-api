package com.kra.api.application;

import com.kra.api.domain.model.ActivityCard;
import com.kra.api.domain.repository.ActivityCardRepository;
import com.kra.api.infrastructure.web.dto.ActivityCardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityCardServiceTest {

    private final ActivityCardRepository repository = mock(ActivityCardRepository.class);
    private final ActivityCardService service = new ActivityCardService(repository);

    @BeforeEach
    void resetMocks() {
        reset(repository);
    }

    @Test
    void getAll_returnsAllThreeCards() {
        List<ActivityCard> cards = List.of(
                new ActivityCard("SHIPPING", "Ship title", "Ship desc", null),
                new ActivityCard("READING", "Read title", "Read desc", null),
                new ActivityCard("PLAYING", "Play title", "Play desc", List.of("tag1", "tag2"))
        );
        when(repository.findAll()).thenReturn(cards);

        List<ActivityCardResponse> result = service.getAll();

        assertEquals(3, result.size());
        assertEquals("SHIPPING", result.get(0).type());
        assertEquals("Ship title", result.get(0).title());
        assertEquals("PLAYING", result.get(2).type());
        assertEquals(List.of("tag1", "tag2"), result.get(2).tags());
    }

    @Test
    void update_existingCard_updatesFieldsAndSaves() {
        ActivityCard existingCard = new ActivityCard("SHIPPING", "Old title", "Old desc", null);
        when(repository.findByType("SHIPPING")).thenReturn(java.util.Optional.of(existingCard));

        ActivityCardResponse result = service.update("SHIPPING", "New title", "New desc", null);

        assertEquals("SHIPPING", result.type());
        assertEquals("New title", result.title());
        assertEquals("New desc", result.description());
        verify(repository).save(any(ActivityCard.class));
    }

    @Test
    void update_cardNotFound_createsNewWithUppercaseType() {
        when(repository.findByType("reading")).thenReturn(java.util.Optional.empty());

        ActivityCardResponse result = service.update("reading", "A title", null, null);

        assertEquals("READING", result.type());
        assertEquals("A title", result.title());
        assertNull(result.description());
        verify(repository).save(any(ActivityCard.class));
    }

    @Test
    void update_withNullFields_doesNotOverwriteExistingValues() {
        ActivityCard existingCard = new ActivityCard("PLAYING", "Existing title", "Existing desc", List.of("chess"));
        when(repository.findByType("PLAYING")).thenReturn(java.util.Optional.of(existingCard));

        ActivityCardResponse result = service.update("PLAYING", null, null, null);

        assertEquals("Existing title", result.title());
        assertEquals("Existing desc", result.description());
        assertEquals(List.of("chess"), result.tags());
        verify(repository).save(any(ActivityCard.class));
    }

    @Test
    void update_withTags_savesTags() {
        when(repository.findByType("PLAYING")).thenReturn(java.util.Optional.empty());
        List<String> tags = List.of("chess", "poker");

        ActivityCardResponse result = service.update("PLAYING", "Play title", "Play desc", tags);

        assertEquals(List.of("chess", "poker"), result.tags());
        verify(repository).save(any(ActivityCard.class));
    }

    @Test
    void update_withBlankStrings_doesNotOverwriteExistingValues() {
        ActivityCard existingCard = new ActivityCard("PLAYING", "Existing title", "Existing desc", List.of("chess"));
        when(repository.findByType("PLAYING")).thenReturn(java.util.Optional.of(existingCard));

        ActivityCardResponse result = service.update("PLAYING", "  ", "\t", null);

        assertEquals("Existing title", result.title());
        assertEquals("Existing desc", result.description());
        verify(repository).save(any(ActivityCard.class));
    }
}
