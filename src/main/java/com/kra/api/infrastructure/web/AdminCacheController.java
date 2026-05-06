package com.kra.api.infrastructure.web;

import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AdminCacheController {

    private final CacheManager cacheManager;

    public AdminCacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostMapping("/admin/cache/flush")
    public ResponseEntity<Map<String, Object>> flushAll() {
        List<String> names = List.copyOf(cacheManager.getCacheNames());
        names.forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        return ResponseEntity.ok(Map.of("flushed", names));
    }
}
