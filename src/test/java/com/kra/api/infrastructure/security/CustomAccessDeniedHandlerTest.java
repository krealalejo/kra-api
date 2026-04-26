package com.kra.api.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    @Test
    void handle() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler(mapper);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException ex = new AccessDeniedException("denied");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        handler.handle(request, response, ex);

        verify(response).setStatus(403);
        verify(response).setContentType("application/json");
    }
}
