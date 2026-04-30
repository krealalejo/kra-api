package com.kra.api.infrastructure.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@DynamoDbBean
public class ProjectMetadataDynamoDbItem extends AbstractDynamoDbItem {

    private String role;
    private String year;
    private String kind;
    private String mainBranch;
    private List<String> stack;

    @DynamoDbAttribute("role")
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @DynamoDbAttribute("year")
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    @DynamoDbAttribute("kind")
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    @DynamoDbAttribute("mainBranch")
    public String getMainBranch() { return mainBranch; }
    public void setMainBranch(String mainBranch) { this.mainBranch = mainBranch; }

    @DynamoDbAttribute("stack")
    public List<String> getStack() { return stack; }
    public void setStack(List<String> stack) { this.stack = stack; }
}
