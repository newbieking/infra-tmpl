package com.infra.event.service;

import com.infra.common.dto.EventMessage;
import com.infra.event.kafka.KafkaProducerService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EventService {

    private final KafkaProducerService kafkaProducer;
    private final RedisTemplate<Object, Object> redisTemplate;

    public EventService(KafkaProducerService kafkaProducer, RedisTemplate<Object, Object> redisTemplate) {
        this.kafkaProducer = kafkaProducer;
        this.redisTemplate = redisTemplate;
    }

    public void publishEvent(EventMessage event) {
        kafkaProducer.send(event.topic(), event.key(), event.payload());
    }

    public void putCache(com.infra.common.dto.CacheEntry entry) {
        if (entry.ttlSeconds() != null && entry.ttlSeconds() > 0) {
            redisTemplate.opsForValue().set(entry.key(), entry.value(), entry.ttlSeconds(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(entry.key(), entry.value());
        }
    }

    public Object getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean deleteCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
