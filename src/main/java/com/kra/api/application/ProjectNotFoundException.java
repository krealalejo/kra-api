package com.kra.api.application;

public class ProjectNotFoundException extends EntityNotFoundException {

    public ProjectNotFoundException(String id) {
        super("Project not found: " + id);
    }
}
