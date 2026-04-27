package com.kra.api.infrastructure.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class AppConfigDynamoDbItem extends AbstractDynamoDbItem {

    private String homePortraitUrl;
    private String cvPortraitUrl;
    private String cvPdfUrl;

    public AppConfigDynamoDbItem() {}

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
