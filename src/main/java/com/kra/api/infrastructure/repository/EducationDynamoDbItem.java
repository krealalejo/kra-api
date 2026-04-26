package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Education;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class EducationDynamoDbItem extends AbstractTimelineDynamoDbItem {

    private String institution;

    public EducationDynamoDbItem() {}

    @DynamoDbAttribute("institution")
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

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
