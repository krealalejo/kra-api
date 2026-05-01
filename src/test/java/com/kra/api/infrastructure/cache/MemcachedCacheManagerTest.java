package com.kra.api.infrastructure.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MemcachedCacheManagerTest {

    @Mock
    private MemcachedClient client;

    @Test
    void getCache_returnsSameInstanceOnRepeatedCalls() {
        var manager = new MemcachedCacheManager(client, Map.of("posts", 1800));

        Cache first = manager.getCache("posts");
        Cache second = manager.getCache("posts");

        assertThat(first).isSameAs(second);
    }

    @Test
    void getCache_knownName_usesTtlFromMap() {
        var manager = new MemcachedCacheManager(client, Map.of("profile", 3600));

        Cache cache = manager.getCache("profile");

        assertThat(cache).isInstanceOf(MemcachedCache.class);
        assertThat(cache.getName()).isEqualTo("profile");
    }

    @Test
    void getCache_unknownName_usesDefaultTtl() {
        var manager = new MemcachedCacheManager(client, Map.of("posts", 1800));

        Cache cache = manager.getCache("unknown");

        assertThat(cache).isInstanceOf(MemcachedCache.class);
        assertThat(cache.getName()).isEqualTo("unknown");
    }

    @Test
    void getCacheNames_returnsConfiguredNames() {
        var manager = new MemcachedCacheManager(client, Map.of("posts", 1800, "profile", 3600));

        assertThat(manager.getCacheNames()).containsExactlyInAnyOrder("posts", "profile");
    }
}
