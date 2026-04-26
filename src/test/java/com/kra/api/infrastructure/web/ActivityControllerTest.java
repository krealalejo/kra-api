package com.kra.api.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kra.api.application.ActivityCardService;
import com.kra.api.infrastructure.config.SecurityConfig;
import com.kra.api.infrastructure.security.CustomAccessDeniedHandler;
import com.kra.api.infrastructure.security.CustomAuthenticationEntryPoint;
import com.kra.api.infrastructure.web.dto.ActivityCardResponse;
import com.kra.api.infrastructure.web.dto.UpdateActivityCardRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
@SuppressWarnings("null")
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityCardService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                new ActivityCardResponse("SHIPPING", "Title", "Desc", List.of("tag"))
        ));

        mockMvc.perform(get("/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SHIPPING"));
    }

    @Test
    void update_withValidType_shouldReturnUpdated() throws Exception {
        UpdateActivityCardRequest request = new UpdateActivityCardRequest();
        request.setTitle("New Title");
        
        ActivityCardResponse response = new ActivityCardResponse("SHIPPING", "New Title", "Desc", null);
        when(service.update(eq("shipping"), eq("New Title"), any(), any())).thenReturn(response);

        mockMvc.perform(put("/activity/shipping")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void update_withInvalidType_shouldReturnBadRequest() throws Exception {
        UpdateActivityCardRequest request = new UpdateActivityCardRequest();
        request.setTitle("Title");

        mockMvc.perform(put("/activity/invalid")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_withoutAuth_shouldReturnUnauthorized() throws Exception {
        UpdateActivityCardRequest request = new UpdateActivityCardRequest();

        mockMvc.perform(put("/activity/shipping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
