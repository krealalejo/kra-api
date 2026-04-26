package com.kra.api.infrastructure.web.dto;

public class UpdateProfileRequest {

    private String homePortraitUrl;
    private String cvPortraitUrl;

    public String getHomePortraitUrl() {
        return homePortraitUrl;
    }

    public void setHomePortraitUrl(String homePortraitUrl) {
        this.homePortraitUrl = homePortraitUrl;
    }

    public String getCvPortraitUrl() {
        return cvPortraitUrl;
    }

    public void setCvPortraitUrl(String cvPortraitUrl) {
        this.cvPortraitUrl = cvPortraitUrl;
    }
}
