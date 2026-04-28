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
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl(), config.getCvPdfUrl());
    }

    public ProfileConfigResponse updateProfile(String homePortraitUrl, String cvPortraitUrl, String cvPdfUrl) {
        AppConfig config = repository.findProfile();
        config.setHomePortraitUrl(homePortraitUrl);
        config.setCvPortraitUrl(cvPortraitUrl);
        config.setCvPdfUrl(cvPdfUrl);
        repository.saveProfile(config);
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl(), config.getCvPdfUrl());
    }
}
