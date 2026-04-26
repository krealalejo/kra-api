package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.SkillCategory;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@DynamoDbBean
public class SkillCategoryDynamoDbItem {

    private String pk;
    private String sk;
    private String gsi1pk;
    private String name;
    private List<String> skills;
    private Integer sortOrder;

    public SkillCategoryDynamoDbItem() {}

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

    @DynamoDbAttribute("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @DynamoDbAttribute("skills")
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    @DynamoDbAttribute("sortOrder")
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public static SkillCategoryDynamoDbItem fromDomain(SkillCategory cat) {
        SkillCategoryDynamoDbItem item = new SkillCategoryDynamoDbItem();
        item.setPk("SKILL#" + cat.getId());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#SKILL");
        item.setName(cat.getName());
        item.setSkills(cat.getSkills().isEmpty() ? null : cat.getSkills());
        item.setSortOrder(cat.getSortOrder());
        return item;
    }

    public SkillCategory toDomain() {
        String id = pk.replace("SKILL#", "");
        return new SkillCategory(id, name, skills, sortOrder != null ? sortOrder : 0);
    }
}
