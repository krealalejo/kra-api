package com.kra.api.application;

public class EducationNotFoundException extends RuntimeException {

    public EducationNotFoundException(String id) {
        super("Education not found: " + id);
    }
}
