package com.kra.api.infrastructure.github;

import com.kra.api.domain.repository.ProjectMetadataRepository;
import com.kra.api.infrastructure.config.GitHubProperties;
import com.kra.api.infrastructure.web.dto.GitHubContributionResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitHubPortfolioClientTest {

    private MockWebServer mockWebServer;
    private GitHubPortfolioClient client;
    private ProjectMetadataRepository projectMetadataRepository;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        GitHubProperties properties = new GitHubProperties("test-token", "test-user", baseUrl);
        projectMetadataRepository = mock(ProjectMetadataRepository.class);
        when(projectMetadataRepository.findByOwnerAndRepo(anyString(), anyString())).thenReturn(null);
        client = new GitHubPortfolioClient(webClient, properties, projectMetadataRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getContributionCalendar_success() throws InterruptedException {
        String body = """
                {
                  "data": {
                    "user": {
                      "contributionsCollection": {
                        "contributionCalendar": {
                          "totalContributions": 10,
                          "weeks": [
                            {
                              "contributionDays": [
                                { "contributionCount": 2, "date": "2024-01-01", "color": "#f0f0f0" }
                              ]
                            }
                          ]
                        }
                      }
                    }
                  }
                }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        GitHubContributionResponse result = client.getContributionCalendar();

        assertEquals(10, result.totalContributions());
        assertEquals(1, result.weeks().size());
        assertEquals(2, result.weeks().get(0).contributionDays().get(0).contributionCount());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/graphql", request.getPath());
        assertTrue(request.getBody().readUtf8().contains("contributionCalendar"));
    }

    @Test
    void listPublicRepos_success() throws InterruptedException {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1",
                    "full_name": "test-user/repo1",
                    "description": "desc",
                    "html_url": "url",
                    "topics": ["topic1"],
                    "stargazers_count": 5,
                    "updated_at": "2024-01-01T00:00:00Z"
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertEquals(1, result.size());
        assertEquals("repo1", result.get(0).name());
        assertEquals("test-user", result.get(0).owner());
        assertEquals(5, result.get(0).stargazersCount());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().contains("/users/test-user/repos"));
    }

    @Test
    void getRepoDetail_success() throws InterruptedException {
        String repoBody = """
                {
                  "owner": { "login": "o" },
                  "name": "n",
                  "full_name": "o/n",
                  "description": "d",
                  "html_url": "u",
                  "topics": [],
                  "stargazers_count": 1,
                  "updated_at": "2024",
                  "default_branch": "main"
                }
                """;
        String readmeBody = """
                {
                  "content": "SGVsbG8gd29ybGQ="
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(repoBody)
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
                .setBody(readmeBody)
                .addHeader("Content-Type", "application/json"));

        var result = client.getRepoDetail("o", "n");

        assertEquals("o/n", result.fullName());
        assertEquals("main", result.defaultBranch());
        assertEquals("Hello world", result.readmeExcerpt());

        assertEquals(2, mockWebServer.getRequestCount());
        mockWebServer.takeRequest();
        RecordedRequest readmeReq = mockWebServer.takeRequest();
        assertTrue(readmeReq.getPath().contains("/repos/o/n/readme"));
    }

    @Test
    void getContributionCalendar_apiError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        GitHubApiException ex = assertThrows(GitHubApiException.class, () -> client.getContributionCalendar());
        assertEquals(404, ex.getHttpStatus());
    }

    @Test
    void listPublicRepos_apiError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        GitHubApiException ex = assertThrows(GitHubApiException.class, () -> client.listPublicRepos());
        assertEquals(500, ex.getHttpStatus());
    }

    @Test
    void listPublicRepos_notArray() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"message\": \"error\"}")
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRepoDetail_noReadme() {
        String repoBody = "{\"full_name\": \"o/n\", \"default_branch\": \"main\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(repoBody)
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        var result = client.getRepoDetail("o", "n");

        assertEquals("o/n", result.fullName());
        assertNull(result.readmeExcerpt());
    }

    @Test
    void getContributionCalendar_userNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"data\": {\"user\": null}}")
                .addHeader("Content-Type", "application/json"));

        GitHubContributionResponse result = client.getContributionCalendar();

        assertEquals(0, result.totalContributions());
        assertTrue(result.weeks().isEmpty());
    }

    @Test
    void listPublicRepos_blankUser_throwsIllegalArgumentException() {
        GitHubProperties blankUserProps = new GitHubProperties("token", "   ", mockWebServer.url("/").toString());
        GitHubPortfolioClient blankClient = new GitHubPortfolioClient(
                WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build(),
                blankUserProps,
                projectMetadataRepository);

        assertThrows(IllegalArgumentException.class, blankClient::listPublicRepos);
    }

    @Test
    void getRepoDetail_longReadme_truncatesContent() throws InterruptedException {
        String repoBody = """
                {
                  "owner": { "login": "o" },
                  "name": "n",
                  "full_name": "o/n",
                  "description": "d",
                  "html_url": "u",
                  "topics": [],
                  "stargazers_count": 1,
                  "updated_at": "2024",
                  "default_branch": "main"
                }
                """;
        String longText = "A".repeat(5000);
        String encoded = java.util.Base64.getEncoder().encodeToString(longText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String readmeBody = "{\"content\": \"" + encoded + "\"}";

        mockWebServer.enqueue(new MockResponse().setBody(repoBody).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody(readmeBody).addHeader("Content-Type", "application/json"));

        var result = client.getRepoDetail("o", "n");

        assertNotNull(result.readmeExcerpt());
        assertTrue(result.readmeExcerpt().endsWith("…"));
        assertTrue(result.readmeExcerpt().length() < 5000);
    }

    @Test
    void getRepoDetail_readmeContentEmpty() {
        String repoBody = "{\"full_name\": \"o/n\", \"default_branch\": \"main\"}";
        String readmeBody = "{\"content\": \"\"}";

        mockWebServer.enqueue(new MockResponse().setBody(repoBody).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody(readmeBody).addHeader("Content-Type", "application/json"));

        var result = client.getRepoDetail("o", "n");

        assertNull(result.readmeExcerpt());
    }

    @Test
    void getContributionCalendar_missingDataNode() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"errors\": [{\"message\": \"error\"}]}")
                .addHeader("Content-Type", "application/json"));

        GitHubContributionResponse result = client.getContributionCalendar();

        assertEquals(0, result.totalContributions());
    }

    @Test
    void listPublicRepos_multipleTopics() {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1",
                    "topics": ["t1", "t2"]
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertEquals(2, result.get(0).topics().size());
    }

    @Test
    void getRepoDetail_repoNotFound_throwsException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(GitHubApiException.class, () -> client.getRepoDetail("o", "n"));
    }
}
