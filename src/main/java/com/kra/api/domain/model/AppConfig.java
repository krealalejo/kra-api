package com.kra.api.domain.model;

public class AppConfig {

    private String homePortraitUrl;
    private String cvPortraitUrl;
    private String cvPdfUrl;

    public AppConfig() {}

    public AppConfig(String homePortraitUrl, String cvPortraitUrl, String cvPdfUrl) {
        this.homePortraitUrl = homePortraitUrl;
        this.cvPortraitUrl = cvPortraitUrl;
        this.cvPdfUrl = cvPdfUrl;
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

    public String getCvPdfUrl() {
        return cvPdfUrl;
    }

    public void setCvPdfUrl(String cvPdfUrl) {
        this.cvPdfUrl = cvPdfUrl;
    }
}
