package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 512)
    private String homePortraitUrl;

    @Size(max = 512)
    private String cvPortraitUrl;

    @Size(max = 512)
    private String cvPdfUrl;

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
