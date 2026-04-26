package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class ProjectDynamoDbItem extends AbstractDynamoDbItem {

    private String title;
    private String description;
    private String url;
    private String content;

    public ProjectDynamoDbItem() {}

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
