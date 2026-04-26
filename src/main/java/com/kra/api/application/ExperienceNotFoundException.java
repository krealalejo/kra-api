package com.kra.api.application;

public class ExperienceNotFoundException extends EntityNotFoundException {

    public ExperienceNotFoundException(String id) {
        super("Experience not found: " + id);
    }
}
