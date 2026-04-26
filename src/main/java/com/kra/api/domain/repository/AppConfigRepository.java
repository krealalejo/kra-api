package com.kra.api.domain.repository;

import com.kra.api.domain.model.AppConfig;

public interface AppConfigRepository {

    AppConfig findProfile();

    void saveProfile(AppConfig config);
}
