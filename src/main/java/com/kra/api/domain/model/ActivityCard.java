package com.kra.api.domain.model;

import java.util.List;

public record ActivityCard(String type, String title, String description, List<String> tags) {}
