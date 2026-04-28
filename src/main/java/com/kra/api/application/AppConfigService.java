package com.kra.api.application;

import com.kra.api.domain.model.AppConfig;
import com.kra.api.domain.repository.AppConfigRepository;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final AppConfigRepository repository;

    public AppConfigService(AppConfigRepository repository) {
        this.repository = repository;
    }

    public ProfileConfigResponse getProfile() {
        AppConfig config = repository.findProfile();
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl());
    }

    public ProfileConfigResponse updateProfile(String homePortraitUrl, String cvPortraitUrl) {
        // Note: this is a last-writer-wins update with no optimistic locking.
        // Concurrent updates will silently overwrite each other. Acceptable for
        // single-admin use; revisit if multiple admins are supported.
        AppConfig config = repository.findProfile();
        config.setHomePortraitUrl(homePortraitUrl);
        config.setCvPortraitUrl(cvPortraitUrl);
        repository.saveProfile(config);
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl());
    }
}
