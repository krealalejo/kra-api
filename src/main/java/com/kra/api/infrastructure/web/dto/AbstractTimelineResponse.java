package com.kra.api.infrastructure.web.dto;

public abstract class AbstractTimelineResponse extends AbstractTimelineRequest {
    protected String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
