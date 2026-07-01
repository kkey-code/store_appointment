package com.wkr.store_appointment.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig implements CachingConfigurer {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer());
        template.setHashValueSerializer(jsonSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()));

        RedisCacheConfiguration detailConfig = defaultConfig.entryTtl(Duration.ofMinutes(10));
        RedisCacheConfiguration statisticsFastConfig = defaultConfig.entryTtl(Duration.ofMinutes(1));

        Map<String, RedisCacheConfiguration> cacheConfigs = Map.ofEntries(
                Map.entry("employee:list", defaultConfig),
                Map.entry("employee:detail", detailConfig),
                Map.entry("customer:page", defaultConfig),
                Map.entry("customer:detail", detailConfig),
                Map.entry("serviceItem:page", defaultConfig),
                Map.entry("serviceItem:detail", detailConfig),
                Map.entry("inventoryItem:page", defaultConfig),
                Map.entry("inventoryItem:detail", detailConfig),
                Map.entry("appointment:page", defaultConfig),
                Map.entry("appointment:getById", detailConfig),
                Map.entry("order:page", defaultConfig),
                Map.entry("order:getById", detailConfig),
                Map.entry("statistics:overview", statisticsFastConfig),
                Map.entry("statistics:orderAmount", defaultConfig)
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {

        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {

                log.warn("读取缓存失败，cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {

                log.warn("写入缓存失败，cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {

                log.warn("删除缓存失败，cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {

                log.warn("清空缓存失败，cache={}", cache.getName(), exception);
            }
        };
    }

    private GenericJackson2JsonRedisSerializer jsonSerializer() {

        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.wkr.store_appointment")
                .allowIfSubType("java.util")
                .allowIfSubType("java.time")
                .allowIfSubType("java.math")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
