package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.github.GitHubPortfolioClient;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import com.kra.api.infrastructure.web.dto.PortfolioRepoDetailResponse;
import com.kra.api.infrastructure.web.dto.PortfolioRepoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class, GlobalExceptionHandler.class})
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubPortfolioClient gitHubPortfolioClient;

    @Test
    void listRepos_returns200() throws Exception {
        when(gitHubPortfolioClient.listPublicRepos()).thenReturn(List.of(
                new PortfolioRepoResponse("o", "n", "o/n", "d", "https://x", List.of("aws"), 3, "2024-01-01T00:00:00Z")));

        mockMvc.perform(get("/portfolio/repos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("o/n"));
    }

    @Test
    void getRepo_returns200() throws Exception {
        PortfolioRepoResponse r = new PortfolioRepoResponse("o", "n", "o/n", "d", "https://x", List.of(), 1, "2024-01-01T00:00:00Z");
        when(gitHubPortfolioClient.getRepoDetail("o", "n")).thenReturn(
                PortfolioRepoDetailResponse.fromSummary(r, "main", "hello"));

        mockMvc.perform(get("/portfolio/repos/o/n"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("o/n"))
                .andExpect(jsonPath("$.defaultBranch").value("main"));
    }

    @Test
    void getRepo_invalidOwner_returns400() throws Exception {
        mockMvc.perform(get("/portfolio/repos/{owner}/{repo}", "x+y", "repo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}
