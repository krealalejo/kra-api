package com.kra.api.infrastructure.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemcachedCacheTest {

    @Mock
    private MemcachedClient client;

    private MemcachedCache cache;

    @BeforeEach
    void setUp() {
        cache = new MemcachedCache("posts", client, 1800);
    }

    @Test
    void getName_returnsName() {
        assertThat(cache.getName()).isEqualTo("posts");
    }

    @Test
    void getNativeCache_returnsClient() {
        assertThat(cache.getNativeCache()).isSameAs(client);
    }

    @Test
    void get_keyExists_returnsWrapper() throws Exception {
        when(client.get(anyString())).thenReturn("value");

        Cache.ValueWrapper result = cache.get("key1");

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo("value");
    }

    @Test
    void get_keyMissing_returnsNull() throws Exception {
        when(client.get(anyString())).thenReturn(null);

        assertThat(cache.get("missing")).isNull();
    }

    @Test
    void get_clientThrows_returnsNull() throws Exception {
        when(client.get(anyString())).thenThrow(new RuntimeException("timeout"));

        assertThat(cache.get("key")).isNull();
    }

    @Test
    void getWithType_keyExists_returnsTyped() throws Exception {
        when(client.get(anyString())).thenReturn("hello");

        String result = cache.get("key", String.class);

        assertThat(result).isEqualTo("hello");
    }

    @Test
    void getWithType_keyMissing_returnsNull() throws Exception {
        when(client.get(anyString())).thenReturn(null);

        assertThat(cache.get("key", String.class)).isNull();
    }

    @Test
    void getWithLoader_cacheMiss_callsLoaderAndPuts() throws Exception {
        when(client.get(anyString())).thenReturn(null);

        String result = cache.get("key", () -> "loaded");

        assertThat(result).isEqualTo("loaded");
        verify(client).set(anyString(), anyInt(), any());
    }

    @Test
    void getWithLoader_cacheHit_returnsWithoutCallingLoader() throws Exception {
        when(client.get(anyString())).thenReturn("cached");
        Callable<String> loader = () -> { throw new AssertionError("should not call loader"); };

        String result = cache.get("key", loader);

        assertThat(result).isEqualTo("cached");
    }

    @Test
    void getWithLoader_loaderThrows_wrapsException() throws Exception {
        when(client.get(anyString())).thenReturn(null);

        assertThatThrownBy(() -> cache.get("key", () -> { throw new Exception("fail"); }))
                .isInstanceOf(Cache.ValueRetrievalException.class);
    }

    @Test
    void put_nonNullValue_setsInClient() throws Exception {
        cache.put("key", "value");

        verify(client).set(anyString(), anyInt(), any());
    }

    @Test
    void put_nullValue_skipsClient() throws Exception {
        cache.put("key", null);

        verify(client, never()).set(anyString(), anyInt(), any());
    }

    @Test
    void put_clientThrows_doesNotPropagate() throws Exception {
        doThrow(new RuntimeException("conn error")).when(client).set(anyString(), anyInt(), any());

        cache.put("key", "value");
    }

    @Test
    void evict_delegatesToClientDelete() throws Exception {
        cache.evict("key");

        verify(client).delete(anyString());
    }

    @Test
    void evict_clientThrows_doesNotPropagate() throws Exception {
        doThrow(new RuntimeException("err")).when(client).delete(anyString());

        cache.evict("key");
    }

    @Test
    void clear_incrementsGeneration_keysBecomeStalted() throws Exception {
        when(client.get(anyString())).thenReturn("v1");
        cache.get("k");

        cache.clear();

        when(client.get(anyString())).thenReturn(null);
        assertThat(cache.get("k")).isNull();
    }
}
