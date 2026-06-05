package com.infra.template.service;

import com.infra.template.dto.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void put(CacheEntry entry) {
        if (entry.ttlSeconds() != null && entry.ttlSeconds() > 0) {
            redisTemplate.opsForValue().set(entry.key(), entry.value(), entry.ttlSeconds(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(entry.key(), entry.value());
        }
        log.debug("Cached key={}", entry.key());
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        return Boolean.TRUE.equals(result);
    }
}
