package com.kra.api.infrastructure.web;

import com.kra.api.application.AppConfigService;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import com.kra.api.infrastructure.web.dto.ProfileConfigResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigController.class)
@Import({ SecurityConfig.class, CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class, GlobalExceptionHandler.class })
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppConfigService appConfigService;

    @Test
    void getProfile_public_returns200WithUrls() throws Exception {
        when(appConfigService.getProfile())
                .thenReturn(new ProfileConfigResponse("portraits/home.webp", "portraits/cv.webp", "documents/cv.pdf"));

        mockMvc.perform(get("/config/profile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homePortraitUrl").value("portraits/home.webp"))
                .andExpect(jsonPath("$.cvPortraitUrl").value("portraits/cv.webp"))
                .andExpect(jsonPath("$.cvPdfUrl").value("documents/cv.pdf"));
    }

    @Test
    void getProfile_public_noUrlsYet_returnsNulls() throws Exception {
        when(appConfigService.getProfile())
                .thenReturn(new ProfileConfigResponse(null, null, null));

        mockMvc.perform(get("/config/profile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homePortraitUrl").doesNotExist());
    }

    @Test
    void updateProfile_withJwt_returns200() throws Exception {
        when(appConfigService.updateProfile("portraits/home.webp", "portraits/cv.webp", null))
                .thenReturn(new ProfileConfigResponse("portraits/home.webp", "portraits/cv.webp", null));

        mockMvc.perform(put("/config/profile")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homePortraitUrl\":\"portraits/home.webp\",\"cvPortraitUrl\":\"portraits/cv.webp\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homePortraitUrl").value("portraits/home.webp"));
    }

    @Test
    void updateProfile_withoutJwt_returns401() throws Exception {
        mockMvc.perform(put("/config/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homePortraitUrl\":\"portraits/home.webp\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void updateProfile_malformedBody_returns400() throws Exception {
        mockMvc.perform(put("/config/profile")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void updateProfile_invalidKeyFormat_returns400() throws Exception {
        mockMvc.perform(put("/config/profile")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homePortraitUrl\":\"images/uuid-legacy.jpg\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void updateProfile_withJwtAndNoAuthorities_returns200() throws Exception {
        when(appConfigService.updateProfile("portraits/home.webp", null, null))
                .thenReturn(new ProfileConfigResponse("portraits/home.webp", null, null));

        mockMvc.perform(put("/config/profile")
                        .with(jwt().authorities())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homePortraitUrl\":\"portraits/home.webp\"}"))
                .andExpect(status().isOk());
    }
}
