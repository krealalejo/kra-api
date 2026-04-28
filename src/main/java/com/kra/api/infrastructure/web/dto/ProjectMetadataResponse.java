package com.kra.api.infrastructure.web.dto;

import java.util.List;

public record ProjectMetadataResponse(String role, String year, String kind, String mainBranch, List<String> stack) {}
