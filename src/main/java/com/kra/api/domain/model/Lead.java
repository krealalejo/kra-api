package com.kra.api.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Lead {

    private final String id;
    private final String email;
    private final String message;
    private final Instant createdAt;

    public Lead(String id, String email, String message, Instant createdAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Lead id must not be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Lead email must not be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Lead message must not be null or blank");
        }
        this.id = id;
        this.email = email;
        this.message = message;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
