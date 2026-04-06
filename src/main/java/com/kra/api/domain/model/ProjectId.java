package com.kra.api.domain.model;

import java.util.Objects;

public final class ProjectId {

    private final String value;

    private ProjectId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProjectId value must not be null or blank");
        }
        this.value = value;
    }

    public static ProjectId of(String value) {
        return new ProjectId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ProjectId{" + value + '}';
    }
}
