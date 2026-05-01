package com.kra.api.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PortfolioRepoDetailResponse(
        String owner,
        String name,
        String fullName,
        String description,
        String htmlUrl,
        List<String> topics,
        int stargazersCount,
        String createdAt,
        String updatedAt,
        String defaultBranch,
        String readmeExcerpt,
        String kind) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static PortfolioRepoDetailResponse fromSummary(
            PortfolioRepoResponse r,
            String defaultBranch,
            String readmeExcerpt) {
        return new PortfolioRepoDetailResponse(
                r.owner(),
                r.name(),
                r.fullName(),
                r.description(),
                r.htmlUrl(),
                r.topics(),
                r.stargazersCount(),
                r.createdAt(),
                r.updatedAt(),
                defaultBranch,
                readmeExcerpt,
                r.kind());
    }
}
