package com.kra.api.infrastructure.web;

import com.kra.api.application.BlogPostNotFoundException;
import com.kra.api.application.ProjectNotFoundException;
import com.kra.api.infrastructure.github.GitHubApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SuppressWarnings("null")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_Project() {
        var ex = new ProjectNotFoundException("p1");
        var res = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("NOT_FOUND", Objects.requireNonNull(res.getBody()).error());
    }

    @Test
    void handleBlogNotFound() {
        var ex = new BlogPostNotFoundException("b1");
        var res = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }


    @Test
    void handleBadArgument() {
        var ex = new IllegalArgumentException("bad");
        var res = handler.handleBadArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void handleGitHub_404() {
        var ex = new GitHubApiException(404, "not found");
        var res = handler.handleGitHub(ex);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void handleGitHub_500() {
        var ex = new GitHubApiException(500, "error");
        var res = handler.handleGitHub(ex);
        assertEquals(HttpStatus.BAD_GATEWAY, res.getStatusCode());
    }

    @Test
    void handleValidation() {
        BindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
        br.addError(new FieldError("obj", "field", "must not be null"));
        // MethodArgumentNotValidException constructor takes (MethodParameter, BindingResult)
        // We mock MethodParameter to satisfy @NonNull requirement
        var ex = new MethodArgumentNotValidException(mock(MethodParameter.class), br);

        var res = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("field: must not be null", Objects.requireNonNull(res.getBody()).message());
    }

    @Test
    void handleGeneric() {
        var ex = new Exception("crash");
        var res = handler.handleGeneric(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
    }
}
