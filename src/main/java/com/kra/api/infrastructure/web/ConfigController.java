package com.kra.api.infrastructure.web;

import com.kra.api.application.AppConfigService;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import com.kra.api.infrastructure.web.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final AppConfigService appConfigService;

    public ConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileConfigResponse> getProfile() {
        return ResponseEntity.ok(appConfigService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileConfigResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileConfigResponse updated = appConfigService.updateProfile(
                request.getHomePortraitUrl(),
                request.getCvPortraitUrl()
        );
        return ResponseEntity.ok(updated);
    }
}
