package com.kra.api.application;

public class EducationNotFoundException extends EntityNotFoundException {

    public EducationNotFoundException(String id) {
        super("Education not found: " + id);
    }
}
