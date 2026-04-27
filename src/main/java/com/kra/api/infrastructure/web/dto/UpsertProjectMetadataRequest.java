package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public class UpsertProjectMetadataRequest {

    private String role;
    private String year;
    private String kind;
    private String mainBranch;

    @Size(max = 30)
    private List<@Size(max = 50) String> stack;

    public UpsertProjectMetadataRequest() {}

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getMainBranch() { return mainBranch; }
    public void setMainBranch(String mainBranch) { this.mainBranch = mainBranch; }

    public List<String> getStack() { return stack; }
    public void setStack(List<String> stack) { this.stack = stack; }
}
