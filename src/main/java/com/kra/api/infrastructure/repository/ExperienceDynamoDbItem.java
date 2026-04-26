package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Experience;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class ExperienceDynamoDbItem {

    private String pk;
    private String sk;
    private String gsi1pk;
    private String title;
    private String company;
    private String location;
    private String years;
    private String description;
    private Integer sortOrder;

    public ExperienceDynamoDbItem() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() { return sk; }
    public void setSk(String sk) { this.sk = sk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    @DynamoDbAttribute("GSI1PK")
    public String getGsi1pk() { return gsi1pk; }
    public void setGsi1pk(String gsi1pk) { this.gsi1pk = gsi1pk; }

    @DynamoDbAttribute("title")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @DynamoDbAttribute("company")
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    @DynamoDbAttribute("location")
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @DynamoDbAttribute("years")
    public String getYears() { return years; }
    public void setYears(String years) { this.years = years; }

    @DynamoDbAttribute("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DynamoDbAttribute("sortOrder")
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

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
