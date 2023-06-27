package com.tradebridge.config;

import java.time.Duration;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createCacheConfigurationWithTtl(Duration.ofDays(1)))
                .initialCacheNames(getInitialCacheNames())
                .build();
        cacheManager.initializeCaches();
        return cacheManager;
    }

    private RedisCacheConfiguration createCacheConfigurationWithTtl(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl);
    }

    private Set<String> getInitialCacheNames() {
        return Set.of("kiteCache");
    }
}
