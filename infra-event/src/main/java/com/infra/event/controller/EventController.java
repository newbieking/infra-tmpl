package com.infra.event.controller;

import com.infra.common.dto.CacheEntry;
import com.infra.common.dto.EventMessage;
import com.infra.event.kafka.KafkaConsumerService;
import com.infra.event.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Event", description = "事件服务（Kafka + Redis）")
public class EventController {

    private final EventService eventService;
    private final KafkaConsumerService consumerService;

    public EventController(EventService eventService, KafkaConsumerService consumerService) {
        this.eventService = eventService;
        this.consumerService = consumerService;
    }

    @PostMapping("/api/v1/events")
    public ResponseEntity<Map<String, String>> publish(@Valid @RequestBody EventMessage event) {
        eventService.publishEvent(event);
        return ResponseEntity.ok(Map.of("status", "published", "topic", event.topic()));
    }

    @GetMapping("/api/v1/events/consumed")
    public ResponseEntity<List<String>> getConsumed() {
        return ResponseEntity.ok(consumerService.getConsumedMessages());
    }

    @PutMapping("/api/v1/cache")
    public ResponseEntity<Map<String, String>> putCache(@Valid @RequestBody CacheEntry entry) {
        eventService.putCache(entry);
        return ResponseEntity.ok(Map.of("status", "cached", "key", entry.key()));
    }

    @GetMapping("/api/v1/cache/{key}")
    public ResponseEntity<Map<String, Object>> getCache(@PathVariable String key) {
        Object value = eventService.getCache(key);
        if (value == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    @DeleteMapping("/api/v1/cache/{key}")
    public ResponseEntity<Map<String, Object>> deleteCache(@PathVariable String key) {
        boolean deleted = eventService.deleteCache(key);
        return ResponseEntity.ok(Map.of("key", key, "deleted", deleted));
    }
}
