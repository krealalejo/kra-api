package com.kra.api.application;

import com.kra.api.infrastructure.repository.AppConfigDynamoDbItem;
import com.kra.api.infrastructure.repository.DynamoDbAppConfigRepository;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final DynamoDbAppConfigRepository repository;

    public AppConfigService(DynamoDbAppConfigRepository repository) {
        this.repository = repository;
    }

    public ProfileConfigResponse getProfile() {
        AppConfigDynamoDbItem item = repository.findProfile();
        return new ProfileConfigResponse(item.getHomePortraitUrl(), item.getCvPortraitUrl());
    }

    public ProfileConfigResponse updateProfile(String homePortraitUrl, String cvPortraitUrl) {
        AppConfigDynamoDbItem item = repository.findProfile();
        if (homePortraitUrl != null) item.setHomePortraitUrl(homePortraitUrl);
        if (cvPortraitUrl != null) item.setCvPortraitUrl(cvPortraitUrl);
        repository.saveProfile(item);
        return new ProfileConfigResponse(item.getHomePortraitUrl(), item.getCvPortraitUrl());
    }
}
