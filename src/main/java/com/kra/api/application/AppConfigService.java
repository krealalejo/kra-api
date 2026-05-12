package com.kra.api.application;

import com.kra.api.domain.model.AppConfig;
import com.kra.api.domain.repository.AppConfigRepository;
import com.kra.api.infrastructure.s3.S3Service;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final AppConfigRepository repository;
    private final S3Service s3Service;

    public AppConfigService(AppConfigRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    @Cacheable("profile")
    public ProfileConfigResponse getProfile() {
        AppConfig config = repository.findProfile();
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl(), config.getCvPdfUrl());
    }

    @CacheEvict(value = "profile", allEntries = true)
    public ProfileConfigResponse updateProfile(String homePortraitUrl, String cvPortraitUrl, String cvPdfUrl) {
        AppConfig config = repository.findProfile();

        deleteIfRemoved(config.getHomePortraitUrl(), homePortraitUrl);
        deleteIfRemoved(config.getCvPortraitUrl(), cvPortraitUrl);
        deleteIfRemoved(config.getCvPdfUrl(), cvPdfUrl);

        config.setHomePortraitUrl(homePortraitUrl);
        config.setCvPortraitUrl(cvPortraitUrl);
        config.setCvPdfUrl(cvPdfUrl);
        repository.saveProfile(config);
        return new ProfileConfigResponse(config.getHomePortraitUrl(), config.getCvPortraitUrl(), config.getCvPdfUrl());
    }

    private void deleteIfRemoved(String oldKey, String newKey) {
        if (oldKey != null && !oldKey.equals(newKey)) {
            s3Service.deleteObject(oldKey);
        }
    }
}
