package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Education;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class EducationDynamoDbItem {

    private String pk;
    private String sk;
    private String gsi1pk;
    private String title;
    private String institution;
    private String location;
    private String years;
    private String description;
    private Integer sortOrder;

    public EducationDynamoDbItem() {}

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

    @DynamoDbAttribute("institution")
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

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

    public static EducationDynamoDbItem fromDomain(Education edu) {
        EducationDynamoDbItem item = new EducationDynamoDbItem();
        item.setPk("EDUCATION#" + edu.getId());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#EDUCATION");
        item.setTitle(edu.getTitle());
        item.setInstitution(edu.getInstitution());
        item.setLocation(edu.getLocation());
        item.setYears(edu.getYears());
        item.setDescription(edu.getDescription());
        item.setSortOrder(edu.getSortOrder());
        return item;
    }

    public Education toDomain() {
        String id = pk.replace("EDUCATION#", "");
        return new Education(id, title, institution, location, years, description,
                sortOrder != null ? sortOrder : 0);
    }
}
