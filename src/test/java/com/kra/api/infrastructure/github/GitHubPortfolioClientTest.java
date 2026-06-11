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
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.lang.reflect.Method;

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
    void getRepoDetail_longReadme_truncatesContent() {
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
    void getRepoDetail_longReadme_keepsFencedCodeBlockIntact() {
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
        // Fence opens just before the 4000-char cut and closes after it.
        String longText = "A".repeat(3990) + "\n```mermaid\ngraph TD\n  X --> Y\n```\n" + "Z".repeat(100);
        String encoded = java.util.Base64.getEncoder().encodeToString(longText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String readmeBody = "{\"content\": \"" + encoded + "\"}";

        mockWebServer.enqueue(new MockResponse().setBody(repoBody).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody(readmeBody).addHeader("Content-Type", "application/json"));

        var result = client.getRepoDetail("o", "n");

        String excerpt = result.readmeExcerpt();
        assertNotNull(excerpt);
        // The whole fenced block must survive: opening fence, body, and closing fence.
        assertTrue(excerpt.contains("```mermaid"));
        assertTrue(excerpt.contains("X --> Y"));
        // Balanced fences — no dangling open block that would break the client renderer.
        int fenceCount = excerpt.split("```", -1).length - 1;
        assertEquals(0, fenceCount % 2);
        assertTrue(excerpt.endsWith("…"));
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

    @Test
    void listPublicRepos_nullUser_throwsIllegalArgumentException() {
        GitHubProperties nullUserProps = new GitHubProperties("token", null, mockWebServer.url("/").toString());
        GitHubPortfolioClient nullUserClient = new GitHubPortfolioClient(
                WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build(),
                nullUserProps,
                projectMetadataRepository);

        assertThrows(IllegalArgumentException.class, nullUserClient::listPublicRepos);
    }

    @Test
    void listPublicRepos_descriptionNull_handlesNullDescription() {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1",
                    "description": null,
                    "topics": []
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertNull(result.get(0).description());
    }

    @Test
    void listPublicRepos_topicsFieldAbsent_returnsEmptyTopics() {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1"
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertTrue(result.get(0).topics().isEmpty());
    }

    @Test
    void listPublicRepos_topicsNotArray_returnsEmptyTopics() {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1",
                    "topics": "not-an-array"
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        var result = client.listPublicRepos();

        assertTrue(result.get(0).topics().isEmpty());
    }

    @Test
    void listPublicRepos_withMetadata_includesKind() {
        String body = """
                [
                  {
                    "owner": { "login": "test-user" },
                    "name": "repo1",
                    "topics": []
                  }
                ]
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        com.kra.api.domain.model.ProjectMetadata meta = new com.kra.api.domain.model.ProjectMetadata("Lead", "2024", "Backend", "main", java.util.List.of("Java"));
        org.mockito.Mockito.when(projectMetadataRepository.findByOwnerAndRepo("test-user", "repo1")).thenReturn(meta);

        var result = client.listPublicRepos();

        assertEquals("Backend", result.get(0).kind());
    }

    @Test
    void listPublicRepos_emptyBody_returnsEmptyList() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        var result = client.listPublicRepos();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRepoDetail_emptyBody_throwsGitHubApiException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertThrows(GitHubApiException.class, () -> client.getRepoDetail("o", "n"));
    }

    @Test
    void getRepoDetail_readmeBodyIsEmpty_returnsNullReadme() {
        String repoBody = "{\"full_name\": \"o/n\", \"default_branch\": \"main\"}";

        mockWebServer.enqueue(new MockResponse().setBody(repoBody).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        var result = client.getRepoDetail("o", "n");

        assertNull(result.readmeExcerpt());
    }

    @Test
    void getRepoDetail_readmeIsJsonNull_returnsNullReadme() {
        String repoBody = "{\"full_name\": \"o/n\", \"default_branch\": \"main\"}";

        mockWebServer.enqueue(new MockResponse().setBody(repoBody).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody("null").addHeader("Content-Type", "application/json"));

        var result = client.getRepoDetail("o", "n");

        assertNull(result.readmeExcerpt());
    }

    @Test
    void getContributionCalendar_emptyBody_returnsEmpty() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        GitHubContributionResponse result = client.getContributionCalendar();

        assertEquals(0, result.totalContributions());
        assertTrue(result.weeks().isEmpty());
    }

    @Test
    void mapUpstream_statusAbove599_returns502() throws Exception {
        WebClientResponseException ex = WebClientResponseException.create(
                600, "Custom", HttpHeaders.EMPTY, new byte[0], null);

        Method method = GitHubPortfolioClient.class.getDeclaredMethod("mapUpstream", WebClientResponseException.class);
        method.setAccessible(true);
        GitHubApiException result = (GitHubApiException) method.invoke(null, ex);

        assertEquals(502, result.getHttpStatus());
    }

    @Test
    void getRepoDetail_readmeErrorNonFourOhFour_returnsNullReadme() {
        String repoBody = "{\"owner\":{\"login\":\"o\"},\"name\":\"n\",\"full_name\":\"o/n\",\"topics\":[],\"default_branch\":\"main\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(repoBody)
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        var result = client.getRepoDetail("o", "n");

        assertNull(result.readmeExcerpt());
    }
}
