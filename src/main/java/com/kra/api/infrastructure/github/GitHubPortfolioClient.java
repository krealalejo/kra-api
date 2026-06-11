package com.kra.api.infrastructure.github;

import tools.jackson.databind.JsonNode;
import com.kra.api.infrastructure.config.GitHubProperties;
import com.kra.api.infrastructure.web.dto.GitHubContributionResponse;
import com.kra.api.infrastructure.web.dto.PortfolioRepoResponse;
import com.kra.api.infrastructure.web.dto.PortfolioRepoDetailResponse;
import com.kra.api.domain.model.ProjectMetadata;
import com.kra.api.domain.repository.ProjectMetadataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class GitHubPortfolioClient {

    private static final int README_PREVIEW_MAX = 4000;

    private final WebClient githubWebClient;
    private final GitHubProperties properties;
    private final ProjectMetadataRepository projectMetadataRepository;

    public GitHubPortfolioClient(
            @Qualifier("githubWebClient") WebClient githubWebClient,
            GitHubProperties properties,
            ProjectMetadataRepository projectMetadataRepository) {
        this.githubWebClient = githubWebClient;
        this.properties = properties;
        this.projectMetadataRepository = projectMetadataRepository;
    }

    @Cacheable("portfolioRepos")
    public List<PortfolioRepoResponse> listPublicRepos() {
        String user = properties.portfolioUser();
        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("github.portfolio-user is not configured");
        }
        try {
            JsonNode root = githubWebClient.get()
                    .uri("/users/{user}/repos?type=owner&sort=updated&per_page=100", user)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            if (root == null || !root.isArray()) {
                return List.of();
            }
            List<PortfolioRepoResponse> out = new ArrayList<>();
            for (JsonNode n : root) {
                out.add(mapRepoWithMetadata(n));
            }
            return out;
        } catch (WebClientResponseException e) {
            throw mapUpstream(e);
        }
    }

    @Cacheable(value = "portfolioRepoDetail", key = "#owner + '/' + #repo")
    public PortfolioRepoDetailResponse getRepoDetail(String owner, String repo) {
        try {
            JsonNode n = githubWebClient.get()
                    .uri("/repos/{owner}/{repo}", owner, repo)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            response -> Mono.error(new GitHubApiException(404, "Repository not found")))
                    .bodyToMono(JsonNode.class)
                    .block();
            if (n == null) {
                throw new GitHubApiException(502, "Empty response from GitHub");
            }
            PortfolioRepoResponse summary = mapRepoWithMetadata(n);
            String defaultBranch = n.path("default_branch").asText(null);
            String readme = fetchReadmeExcerpt(owner, repo);
            return PortfolioRepoDetailResponse.fromSummary(summary, defaultBranch, readme);
        } catch (WebClientResponseException e) {
            throw mapUpstream(e);
        }
    }

    private String fetchReadmeExcerpt(String owner, String repo) {
        try {
            JsonNode readme = githubWebClient.get()
                    .uri("/repos/{owner}/{repo}/readme", owner, repo)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            if (readme == null || readme.isNull()) {
                return null;
            }
            String b64 = readme.path("content").asText("");
            if (b64.isBlank()) {
                return null;
            }
            b64 = b64.replaceAll("\\s", "");
            byte[] raw = Base64.getMimeDecoder().decode(b64);
            String text = new String(raw, StandardCharsets.UTF_8);
            if (text.length() > README_PREVIEW_MAX) {
                return truncatePreservingFences(text);
            }
            return text;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            return null;
        }
    }

    /**
     * Truncates README text to {@link #README_PREVIEW_MAX} without breaking a fenced code block.
     * If the cut lands inside an open ``` fence (odd fence count), the excerpt is extended to the
     * block's closing fence so client-side renderers (e.g. Mermaid diagrams) receive valid source.
     * If no closing fence exists, the dangling open fence is dropped instead.
     */
    private String truncatePreservingFences(String text) {
        int end = README_PREVIEW_MAX;
        String head = text.substring(0, end);
        if (countFences(head) % 2 != 0) {
            int closing = text.indexOf("```", end);
            if (closing >= 0) {
                end = closing + 3;
            } else {
                return head.substring(0, head.lastIndexOf("```")).stripTrailing() + "…";
            }
        }
        if (end >= text.length()) {
            return text;
        }
        return text.substring(0, end) + "\n\n…";
    }

    private int countFences(String text) {
        int count = 0;
        int idx = text.indexOf("```");
        while (idx >= 0) {
            count++;
            idx = text.indexOf("```", idx + 3);
        }
        return count;
    }

    public GitHubContributionResponse getContributionCalendar() {
        String user = properties.portfolioUser();
        String query = """
                {
                  user(login: "%s") {
                    contributionsCollection {
                      contributionCalendar {
                        totalContributions
                        weeks {
                          contributionDays {
                            contributionCount
                            date
                            color
                          }
                        }
                      }
                    }
                  }
                }
                """.formatted(user);

        try {
            JsonNode response = githubWebClient.post()
                    .uri("/graphql")
                    .bodyValue(java.util.Objects.requireNonNull(java.util.Map.of("query", query)))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || response.path("data").path("user").isMissingNode()) {
                return new GitHubContributionResponse(0, List.of());
            }

            JsonNode calendar = response.path("data").path("user")
                    .path("contributionsCollection")
                    .path("contributionCalendar");

            int total = calendar.path("totalContributions").asInt(0);
            List<GitHubContributionResponse.ContributionWeek> weeks = new ArrayList<>();

            for (JsonNode weekNode : calendar.path("weeks")) {
                List<GitHubContributionResponse.ContributionDay> days = new ArrayList<>();
                for (JsonNode dayNode : weekNode.path("contributionDays")) {
                    days.add(new GitHubContributionResponse.ContributionDay(
                            dayNode.path("contributionCount").asInt(0),
                            dayNode.path("date").asText(""),
                            dayNode.path("color").asText("")
                    ));
                }
                weeks.add(new GitHubContributionResponse.ContributionWeek(days));
            }

            return new GitHubContributionResponse(total, weeks);
        } catch (WebClientResponseException e) {
            throw mapUpstream(e);
        }
    }

    private static GitHubApiException mapUpstream(WebClientResponseException e) {
        int code = e.getStatusCode().value();
        if (code == 404) {
            return new GitHubApiException(404, "Repository not found");
        }
        return new GitHubApiException(code >= 400 && code < 600 ? code : 502, "GitHub API error");
    }

    private PortfolioRepoResponse mapRepoWithMetadata(JsonNode n) {
        String owner = n.path("owner").path("login").asText("");
        String name = n.path("name").asText("");
        String fullName = n.path("full_name").asText("");
        String description = n.path("description").isNull() ? null : n.path("description").asText(null);
        String htmlUrl = n.path("html_url").asText("");
        List<String> topics = new ArrayList<>();
        JsonNode t = n.get("topics");
        if (t != null && t.isArray()) {
            for (JsonNode x : t) {
                topics.add(x.asText());
            }
        }
        int stars = n.path("stargazers_count").asInt(0);
        String createdAt = n.path("created_at").asText("");
        String updatedAt = n.path("updated_at").asText("");

        ProjectMetadata meta = projectMetadataRepository.findByOwnerAndRepo(owner, name);
        String kind = meta != null ? meta.kind() : null;

        return new PortfolioRepoResponse(owner, name, fullName, description, htmlUrl, topics, stars, createdAt, updatedAt, kind);
    }
}
