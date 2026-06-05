package com.infra.template.service;

import com.infra.template.dto.CacheEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @Test
    void putWithTtlShouldSetWithExpiration() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        CacheEntry entry = new CacheEntry("key1", "value1", 60L);

        cacheService.put(entry);

        verify(valueOperations).set("key1", "value1", 60L, TimeUnit.SECONDS);
    }

    @Test
    void putWithoutTtlShouldSetWithoutExpiration() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        CacheEntry entry = new CacheEntry("key1", "value1", null);

        cacheService.put(entry);

        verify(valueOperations).set("key1", "value1");
    }

    @Test
    void putWithZeroTtlShouldSetWithoutExpiration() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        CacheEntry entry = new CacheEntry("key1", "value1", 0L);

        cacheService.put(entry);

        verify(valueOperations).set("key1", "value1");
    }

    @Test
    void getShouldReturnValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("key1")).thenReturn("value1");

        Object result = cacheService.get("key1");

        assertEquals("value1", result);
    }

    @Test
    void getNonExistentKeyShouldReturnNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("missing")).thenReturn(null);

        Object result = cacheService.get("missing");

        assertNull(result);
    }

    @Test
    void deleteExistingKeyShouldReturnTrue() {
        when(redisTemplate.delete("key1")).thenReturn(true);

        boolean result = cacheService.delete("key1");

        assertTrue(result);
    }

    @Test
    void deleteNonExistentKeyShouldReturnFalse() {
        when(redisTemplate.delete("missing")).thenReturn(false);

        boolean result = cacheService.delete("missing");

        assertFalse(result);
    }
}
