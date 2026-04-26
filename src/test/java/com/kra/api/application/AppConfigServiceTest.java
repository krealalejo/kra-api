package com.kra.api.application;

import com.kra.api.domain.model.AppConfig;
import com.kra.api.domain.repository.AppConfigRepository;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppConfigServiceTest {

    @Mock
    private AppConfigRepository repository;

    @InjectMocks
    private AppConfigService service;

    @Test
    void getProfile_shouldReturnResponse() {
        AppConfig config = new AppConfig("url1", "url2");
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.getProfile();

        assertEquals("url1", result.homePortraitUrl());
        assertEquals("url2", result.cvPortraitUrl());
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        AppConfig config = new AppConfig("old1", "old2");
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.updateProfile("new1", "new2");

        assertEquals("new1", result.homePortraitUrl());
        assertEquals("new2", result.cvPortraitUrl());
        verify(repository).saveProfile(config);
    }

    @Test
    void updateProfile_shouldNotUpdateNullFields() {
        AppConfig config = new AppConfig("old1", "old2");
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.updateProfile(null, null);

        assertEquals("old1", result.homePortraitUrl());
        assertEquals("old2", result.cvPortraitUrl());
        verify(repository).saveProfile(config);
    }
}
