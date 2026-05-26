package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.cloudwatch.CloudWatchMetricsService;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMetricsController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class AdminMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CloudWatchMetricsService metricsService;

    @Test
    void getMetrics_withAuth_returns200WithData() throws Exception {
        when(metricsService.getSystemMetrics()).thenReturn(Map.of(
                "thumb_invocations", Map.of("timestamps", java.util.List.of(), "values", java.util.List.of())
        ));

        mockMvc.perform(get("/admin/metrics").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thumb_invocations").exists());
    }

    @Test
    void getMetrics_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/admin/metrics"))
                .andExpect(status().isUnauthorized());
    }
}
