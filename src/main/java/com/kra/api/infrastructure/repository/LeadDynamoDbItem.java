package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Lead;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@DynamoDbBean
public class LeadDynamoDbItem extends AbstractDynamoDbItem {

    private String email;
    private String message;
    private Long createdAtMillis;

    public LeadDynamoDbItem() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public void setCreatedAtMillis(Long createdAtMillis) {
        this.createdAtMillis = createdAtMillis;
    }

    public static LeadDynamoDbItem fromDomain(Lead lead) {
        LeadDynamoDbItem item = new LeadDynamoDbItem();
        item.setPk("LEAD#" + lead.getId());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#LEAD");
        item.setEmail(lead.getEmail());
        item.setMessage(lead.getMessage());
        item.setCreatedAtMillis(lead.getCreatedAt().toEpochMilli());
        return item;
    }

    public Lead toDomain() {
        String id = pk.replace("LEAD#", "");
        Instant created = Instant.ofEpochMilli(createdAtMillis != null ? createdAtMillis : 0L);
        return new Lead(id, email, message, created);
    }
}
