package com.kra.api.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public final class BlogSlug {

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9-]{1,128}$");

    private final String value;

    private BlogSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Blog slug must not be null or blank");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Blog slug must match ^[a-z0-9-]{1,128}$");
        }
        this.value = value;
    }

    public static BlogSlug of(String value) {
        return new BlogSlug(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogSlug blogSlug)) return false;
        return Objects.equals(value, blogSlug.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BlogSlug{" + value + '}';
    }
}
