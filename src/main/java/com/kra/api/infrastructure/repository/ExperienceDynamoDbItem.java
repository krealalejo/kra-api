package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Experience;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class ExperienceDynamoDbItem extends AbstractTimelineDynamoDbItem {

    private String company;

    public ExperienceDynamoDbItem() {}

    @DynamoDbAttribute("company")
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public static ExperienceDynamoDbItem fromDomain(Experience exp) {
        ExperienceDynamoDbItem item = new ExperienceDynamoDbItem();
        item.setPk("EXPERIENCE#" + exp.getId());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#EXPERIENCE");
        item.setTitle(exp.getTitle());
        item.setCompany(exp.getCompany());
        item.setLocation(exp.getLocation());
        item.setYears(exp.getYears());
        item.setDescription(exp.getDescription());
        item.setSortOrder(exp.getSortOrder());
        return item;
    }

    public Experience toDomain() {
        String id = pk.replace("EXPERIENCE#", "");
        return new Experience(id, title, company, location, years, description,
                sortOrder != null ? sortOrder : 0);
    }
}
