package com.kra.api.domain.model;

import java.util.Objects;

public record Reference(String label, String url) {
    public Reference {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(url, "url must not be null");
    }
}
