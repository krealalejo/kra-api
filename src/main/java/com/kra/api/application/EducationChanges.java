package com.kra.api.application;

public record EducationChanges(
        String title,
        String institution,
        String location,
        String years,
        String description,
        Integer sortOrder,
        String logoUrl,
        boolean updateLogoUrl) {
}
