package com.kra.api.infrastructure.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

public abstract class AbstractTimelineDynamoDbItem extends AbstractDynamoDbItem {
    protected String title;
    protected String location;
    protected String years;
    protected String description;
    protected Integer sortOrder;

    @DynamoDbAttribute("title")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @DynamoDbAttribute("location")
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @DynamoDbAttribute("years")
    public String getYears() { return years; }
    public void setYears(String years) { this.years = years; }

    @DynamoDbAttribute("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DynamoDbAttribute("sortOrder")
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
