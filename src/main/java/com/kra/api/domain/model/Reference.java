package com.kra.api.domain.model;

import java.io.Serializable;
import java.util.Objects;

public record Reference(String label, String url) implements Serializable {
    public Reference {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(url, "url must not be null");
    }
}
