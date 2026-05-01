package com.kra.api.infrastructure.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemcachedCacheManager implements CacheManager {

    private static final int DEFAULT_TTL = 1800;

    private final MemcachedClient client;
    private final Map<String, Integer> ttls;
    private final ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();

    public MemcachedCacheManager(MemcachedClient client, Map<String, Integer> ttls) {
        this.client = client;
        this.ttls = ttls;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, n ->
                new MemcachedCache(n, client, ttls.getOrDefault(n, DEFAULT_TTL)));
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(ttls.keySet());
    }
}
