package com.kra.api.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PortfolioRepoResponse(
        String owner,
        String name,
        String fullName,
        String description,
        String htmlUrl,
        List<String> topics,
        int stargazersCount,
        String createdAt,
        String updatedAt,
        String kind) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
