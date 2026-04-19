package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.github.GitHubPortfolioClient;
import com.kra.api.infrastructure.web.dto.GitHubContributionResponse;
import com.kra.api.infrastructure.web.dto.PortfolioRepoDetailResponse;
import com.kra.api.infrastructure.web.dto.PortfolioRepoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private static final Pattern SEGMENT = Pattern.compile("^[a-zA-Z0-9_.-]{1,100}$");

    private final GitHubPortfolioClient gitHubPortfolioClient;

    public PortfolioController(GitHubPortfolioClient gitHubPortfolioClient) {
        this.gitHubPortfolioClient = gitHubPortfolioClient;
    }

    @GetMapping("/repos")
    public List<PortfolioRepoResponse> listRepos() {
        return gitHubPortfolioClient.listPublicRepos();
    }

    @GetMapping("/contributions")
    public GitHubContributionResponse getContributions() {
        return gitHubPortfolioClient.getContributionCalendar();
    }

    @GetMapping("/repos/{owner}/{repo}")
    public PortfolioRepoDetailResponse getRepo(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo) {
        if (!SEGMENT.matcher(owner).matches() || !SEGMENT.matcher(repo).matches()) {
            throw new IllegalArgumentException("Invalid owner or repo");
        }
        return gitHubPortfolioClient.getRepoDetail(owner, repo);
    }
}
