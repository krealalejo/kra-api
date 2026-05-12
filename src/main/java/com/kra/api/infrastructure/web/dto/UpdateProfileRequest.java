package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 512)
    @Pattern(regexp = "^portraits/(home|cv)\\.webp$",
             message = "Must be a valid portrait key: portraits/home.webp or portraits/cv.webp")
    private String homePortraitUrl;

    @Size(max = 512)
    @Pattern(regexp = "^portraits/(home|cv)\\.webp$",
             message = "Must be a valid portrait key: portraits/home.webp or portraits/cv.webp")
    private String cvPortraitUrl;

    @Size(max = 512)
    @Pattern(regexp = "^documents/cv\\.pdf$",
             message = "Must be: documents/cv.pdf")
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
