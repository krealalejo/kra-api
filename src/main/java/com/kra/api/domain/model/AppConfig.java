package com.kra.api.domain.model;

public class AppConfig {

    private String homePortraitUrl;
    private String cvPortraitUrl;

    public AppConfig() {}

    public AppConfig(String homePortraitUrl, String cvPortraitUrl) {
        this.homePortraitUrl = homePortraitUrl;
        this.cvPortraitUrl = cvPortraitUrl;
    }

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
