package com.kra.api.infrastructure.web.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateActivityCardRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private List<@Size(max = 50) String> tags;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
