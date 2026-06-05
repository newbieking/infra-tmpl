package com.infra.event.service;

import com.infra.common.dto.CacheEntry;
import com.infra.common.dto.EventMessage;
import com.infra.event.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private KafkaProducerService kafkaProducer;

    @Mock
    private RedisTemplate<Object, Object> redisTemplate;

    @Mock
    private ValueOperations<Object, Object> valueOperations;

    @InjectMocks
    private EventService eventService;

    @Test
    void publishEventShouldDelegateToProducer() {
        EventMessage event = new EventMessage("topic1", "key1", "payload", null, null);
        eventService.publishEvent(event);
        verify(kafkaProducer).send("topic1", "key1", "payload");
    }

    @Test
    void putCacheWithTtlShouldSetExpiration() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        eventService.putCache(new CacheEntry("k1", "v1", 60L));
        verify(valueOperations).set("k1", "v1", 60L, TimeUnit.SECONDS);
    }

    @Test
    void putCacheWithoutTtlShouldSet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        eventService.putCache(new CacheEntry("k1", "v1", null));
        verify(valueOperations).set("k1", "v1");
    }

    @Test
    void getCacheShouldReturnValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("k1")).thenReturn("v1");
        assertEquals("v1", eventService.getCache("k1"));
    }

    @Test
    void getCacheNonExistentShouldReturnNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("missing")).thenReturn(null);
        assertNull(eventService.getCache("missing"));
    }

    @Test
    void deleteCacheShouldReturnTrue() {
        when(redisTemplate.delete("k1")).thenReturn(true);
        assertTrue(eventService.deleteCache("k1"));
    }

    @Test
    void deleteCacheNonExistentShouldReturnFalse() {
        when(redisTemplate.delete("missing")).thenReturn(false);
        assertFalse(eventService.deleteCache("missing"));
    }
}
