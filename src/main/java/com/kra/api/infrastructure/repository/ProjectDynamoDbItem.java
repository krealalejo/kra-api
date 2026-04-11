package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class ProjectDynamoDbItem {

    private String pk;
    private String sk;
    private String gsi1pk;
    private String title;
    private String description;
    private String url;
    private String content;

    public ProjectDynamoDbItem() {}

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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public static ProjectDynamoDbItem fromDomain(Project project) {
        ProjectDynamoDbItem item = new ProjectDynamoDbItem();
        item.setPk("PROJECT#" + project.getId().getValue());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#PROJECT");
        item.setTitle(project.getTitle());
        item.setDescription(project.getDescription());
        item.setUrl(project.getUrl());
        item.setContent(project.getContent());
        return item;
    }

    public Project toDomain() {
        String rawId = pk.replace("PROJECT#", "");
        return new Project(
            ProjectId.of(rawId),
            title,
            description,
            url,
            content
        );
    }
}
