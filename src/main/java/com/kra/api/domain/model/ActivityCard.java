package com.kra.api.domain.model;

import java.util.List;

public class ActivityCard {

    private String type;
    private String title;
    private String description;
    private List<String> tags;

    public ActivityCard() {}

    public ActivityCard(String type, String title, String description, List<String> tags) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
