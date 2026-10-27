package com.kra.api.application;

public record ExperienceChanges(
        String title,
        String company,
        String location,
        String years,
        String description,
        Integer sortOrder,
        String logoUrl,
        boolean updateLogoUrl) {
}
