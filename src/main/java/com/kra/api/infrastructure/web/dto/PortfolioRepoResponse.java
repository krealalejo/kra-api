package com.kra.api.infrastructure.web.dto;

import java.util.List;

public record PortfolioRepoResponse(
        String owner,
        String name,
        String fullName,
        String description,
        String htmlUrl,
        List<String> topics,
        int stargazersCount,
        String updatedAt,
        String kind) {
}
