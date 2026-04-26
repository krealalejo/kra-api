package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 512)
    @Pattern(regexp = "^images/[\\w\\-]+\\.(jpg|jpeg|png|webp)$",
             message = "Must be a valid relative S3 image key under images/")
    private String homePortraitUrl;

    @Size(max = 512)
    @Pattern(regexp = "^images/[\\w\\-]+\\.(jpg|jpeg|png|webp)$",
             message = "Must be a valid relative S3 image key under images/")
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
