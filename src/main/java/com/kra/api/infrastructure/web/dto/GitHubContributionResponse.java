package com.kra.api.infrastructure.web.dto;

import java.util.List;

public record GitHubContributionResponse(
        int totalContributions,
        List<ContributionWeek> weeks
) {
    public record ContributionWeek(
            List<ContributionDay> contributionDays
    ) {}

    public record ContributionDay(
            int contributionCount,
            String date,
            String color
    ) {}
}
