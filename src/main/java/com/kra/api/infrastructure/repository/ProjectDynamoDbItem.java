package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Infrastructure DTO for DynamoDB Enhanced Client.
 * Bridges the domain Project aggregate and the annotated bean the Enhanced Client requires.
 * Lives ONLY in the infrastructure layer — domain model stays annotation-free.
 *
 * DynamoDB key design (matches kra-table from Phase 3 Terraform):
 *   PK  = "PROJECT#<id>"
 *   SK  = "METADATA"
 *   GSI1PK = "TYPE#PROJECT"  (enables AP1: list all projects via GSI1 query)
 */
@DynamoDbBean
public class ProjectDynamoDbItem {

    private String pk;       // "PROJECT#<id>"
    private String sk;       // "METADATA"
    private String gsi1pk;   // "TYPE#PROJECT"
    private String title;
    private String description;
    private String url;
    private String content;

    /** Required no-arg constructor for @DynamoDbBean. */
    public ProjectDynamoDbItem() {}

    @DynamoDbPartitionKey
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

    @DynamoDbSortKey
    public String getSk() { return sk; }
    public void setSk(String sk) { this.sk = sk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
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

    /** Converts domain Project → infrastructure DynamoDB item. */
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

    /** Converts DynamoDB item → domain Project. Strips the "PROJECT#" prefix from pk. */
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
