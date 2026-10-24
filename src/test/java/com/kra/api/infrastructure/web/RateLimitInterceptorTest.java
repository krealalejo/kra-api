package com.kra.api.infrastructure.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitInterceptorTest {

    private RateLimitInterceptor interceptor;
    private final Object handler = new Object();

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitInterceptor();
    }

    @Test
    void allowsUpToCapacityRequests() throws Exception {
        MockHttpServletRequest request = requestFrom("1.2.3.4");

        for (int i = 0; i < RateLimitInterceptor.CAPACITY; i++) {
            assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), handler));
        }
    }

    @Test
    void blocksRequestBeyondCapacity() throws Exception {
        MockHttpServletRequest request = requestFrom("5.6.7.8");
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (int i = 0; i < RateLimitInterceptor.CAPACITY; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), handler);
        }

        assertFalse(interceptor.preHandle(request, response, handler));
        assertEquals(429, response.getStatus());
    }

    @Test
    void blockedResponseHasJsonContentType() throws Exception {
        MockHttpServletRequest request = requestFrom("9.9.9.9");
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (int i = 0; i <= RateLimitInterceptor.CAPACITY; i++) {
            interceptor.preHandle(request, response, handler);
        }

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertTrue(response.getContentAsString().contains("TOO_MANY_REQUESTS"));
    }

    @Test
    void differentIpsHaveIndependentBuckets() throws Exception {
        MockHttpServletRequest req1 = requestFrom("10.0.0.1");
        MockHttpServletRequest req2 = requestFrom("10.0.0.2");

        for (int i = 0; i < RateLimitInterceptor.CAPACITY; i++) {
            interceptor.preHandle(req1, new MockHttpServletResponse(), handler);
        }

        assertTrue(interceptor.preHandle(req2, new MockHttpServletResponse(), handler));
    }

    @Test
    void usesRightMostIpFromXForwardedForHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.1, 198.51.100.7");
        request.setRemoteAddr("172.18.0.2");

        assertEquals("198.51.100.7", interceptor.resolveClientIp(request));
    }

    @Test
    void spoofedLeftMostXForwardedForDoesNotChangeKey() {
        MockHttpServletRequest honest = new MockHttpServletRequest();
        honest.addHeader("X-Forwarded-For", "198.51.100.7");
        honest.setRemoteAddr("172.18.0.2");

        MockHttpServletRequest spoofed = new MockHttpServletRequest();
        spoofed.addHeader("X-Forwarded-For", "1.1.1.1, 198.51.100.7");
        spoofed.setRemoteAddr("172.18.0.2");

        assertEquals(interceptor.resolveClientIp(honest), interceptor.resolveClientIp(spoofed));
    }

    @Test
    void rotatingSpoofedXForwardedForStillHitsLimit() throws Exception {
        for (int i = 0; i < RateLimitInterceptor.CAPACITY; i++) {
            assertTrue(interceptor.preHandle(forwardedRequest("10.9.9." + i + ", 198.51.100.7"), new MockHttpServletResponse(), handler));
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(forwardedRequest("10.9.9.99, 198.51.100.7"), response, handler));
        assertEquals(429, response.getStatus());
    }

    @Test
    void fallsBackToRemoteAddrWhenRightMostXForwardedForIsEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.1,");
        request.setRemoteAddr("172.18.0.2");

        assertEquals("172.18.0.2", interceptor.resolveClientIp(request));
    }

    @Test
    void fallsBackToRemoteAddrWhenNoXForwardedFor() {
        MockHttpServletRequest request = requestFrom("192.168.1.1");
        assertEquals("192.168.1.1", interceptor.resolveClientIp(request));
    }

    private MockHttpServletRequest forwardedRequest(String forwardedFor) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", forwardedFor);
        request.setRemoteAddr("172.18.0.2");
        return request;
    }

    private MockHttpServletRequest requestFrom(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(ip);
        return request;
    }
}
