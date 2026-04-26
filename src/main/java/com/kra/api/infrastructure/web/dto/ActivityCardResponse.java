package com.kra.api.infrastructure.web.dto;

import java.util.List;

public record ActivityCardResponse(String type, String title, String description, List<String> tags) {}
