package com.kra.api.domain.model;

import java.util.List;

public record ProjectMetadata(String role, String year, String kind, String mainBranch, List<String> stack) {}
