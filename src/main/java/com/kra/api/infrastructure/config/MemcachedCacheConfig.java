package com.kra.api.infrastructure.config;

import com.kra.api.infrastructure.cache.MemcachedCacheManager;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@Profile("!local")
public class MemcachedCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(MemcachedCacheConfig.class);

    @Value("${memcached.host:localhost}")
    private String host;

    @Value("${memcached.port:11211}")
    private int port;

    @Bean(destroyMethod = "shutdown")
    public MemcachedClient memcachedClient() throws IOException {
        log.info("Connecting to Memcached at {}:{}", host, port);
        return new XMemcachedClientBuilder(AddrUtil.getAddresses(host + ":" + port)).build();
    }

    @Bean
    public CacheManager cacheManager(MemcachedClient memcachedClient) {
        Map<String, Integer> ttls = new HashMap<>();
        ttls.put("activity", 1800);
        ttls.put("posts", 1800);
        ttls.put("post", 1800);
        ttls.put("profile", 3600);
        ttls.put("education", 3600);
        ttls.put("experience", 3600);
        ttls.put("skillCategories", 3600);
        ttls.put("projects", 1800);
        ttls.put("projectMetadata", 3600);
        ttls.put("portfolioRepos", 900);
        ttls.put("portfolioRepoDetail", 900);
        return new MemcachedCacheManager(memcachedClient, ttls);
    }
}
