package com.kra.api.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCacheController.class)
class AdminCacheControllerTest extends AbstractControllerTest {

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void flushAll_withJwt_returns200AndFlushedNames() throws Exception {
        var cache = new NoOpCache("posts");
        when(cacheManager.getCacheNames()).thenReturn(List.of("posts", "activity"));
        when(cacheManager.getCache("posts")).thenReturn(cache);
        when(cacheManager.getCache("activity")).thenReturn(null);

        mockMvc.perform(post("/admin/cache/flush").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flushed").isArray());
    }

    @Test
    void flushAll_withoutJwt_returns401() throws Exception {
        mockMvc.perform(post("/admin/cache/flush"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void flushAll_clearsEachCache() throws Exception {
        var cache = mock(org.springframework.cache.Cache.class);
        when(cacheManager.getCacheNames()).thenReturn(List.of("posts"));
        when(cacheManager.getCache("posts")).thenReturn(cache);

        mockMvc.perform(post("/admin/cache/flush").with(jwt()))
                .andExpect(status().isOk());

        verify(cache).clear();
    }
}
