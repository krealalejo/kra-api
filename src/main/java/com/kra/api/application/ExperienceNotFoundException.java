package com.kra.api.application;

public class ExperienceNotFoundException extends RuntimeException {

    public ExperienceNotFoundException(String id) {
        super("Experience not found: " + id);
    }
}
