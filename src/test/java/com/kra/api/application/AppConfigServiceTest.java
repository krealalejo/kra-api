package com.kra.api.application;

import com.kra.api.domain.model.AppConfig;
import com.kra.api.domain.repository.AppConfigRepository;
import com.kra.api.infrastructure.s3.S3Service;
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

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AppConfigService service;

    @Test
    void getProfile_shouldReturnResponse() {
        AppConfig config = new AppConfig("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf");
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.getProfile();

        assertEquals("portraits/home.webp", result.homePortraitUrl());
        assertEquals("portraits/cv.webp", result.cvPortraitUrl());
        assertEquals("documents/cv.pdf", result.cvPdfUrl());
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        AppConfig config = new AppConfig(null, null, null);
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.updateProfile("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf");

        assertEquals("portraits/home.webp", result.homePortraitUrl());
        assertEquals("portraits/cv.webp", result.cvPortraitUrl());
        assertEquals("documents/cv.pdf", result.cvPdfUrl());
        verify(repository).saveProfile(config);
        verifyNoInteractions(s3Service);
    }

    @Test
    void updateProfile_shouldAllowNullFields() {
        AppConfig config = new AppConfig("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf");
        when(repository.findProfile()).thenReturn(config);

        ProfileConfigResponse result = service.updateProfile(null, null, null);

        assertNull(result.homePortraitUrl());
        assertNull(result.cvPortraitUrl());
        assertNull(result.cvPdfUrl());
        verify(repository).saveProfile(config);
    }

    @Test
    void updateProfile_whenPortraitRemoved_deletesOldKeyFromS3() {
        AppConfig config = new AppConfig("portraits/home.webp", "portraits/cv.webp", null);
        when(repository.findProfile()).thenReturn(config);

        service.updateProfile(null, "portraits/cv.webp", null);

        verify(s3Service).deleteObject("portraits/home.webp");
        verify(s3Service, never()).deleteObject("portraits/cv.webp");
    }

    @Test
    void updateProfile_whenPortraitReplaced_deletesOldKeyFromS3() {
        AppConfig config = new AppConfig("portraits/home.webp", null, null);
        when(repository.findProfile()).thenReturn(config);

        service.updateProfile("portraits/home.webp", null, null);

        verify(s3Service, never()).deleteObject(any());
    }

    @Test
    void updateProfile_whenPdfRemoved_deletesOldKeyFromS3() {
        AppConfig config = new AppConfig(null, null, "documents/cv.pdf");
        when(repository.findProfile()).thenReturn(config);

        service.updateProfile(null, null, null);

        verify(s3Service).deleteObject("documents/cv.pdf");
    }

    @Test
    void updateProfile_whenKeyUnchanged_doesNotDeleteFromS3() {
        AppConfig config = new AppConfig("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf");
        when(repository.findProfile()).thenReturn(config);

        service.updateProfile("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf");

        verifyNoInteractions(s3Service);
    }
}
