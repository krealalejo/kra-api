package com.kra.api.domain.model;

import java.util.List;

public class ProjectMetadata {

    private String role;
    private String year;
    private String kind;
    private String mainBranch;
    private List<String> stack;

    public ProjectMetadata() {}

    public ProjectMetadata(String role, String year, String kind, String mainBranch, List<String> stack) {
        this.role = role;
        this.year = year;
        this.kind = kind;
        this.mainBranch = mainBranch;
        this.stack = stack;
    }

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
