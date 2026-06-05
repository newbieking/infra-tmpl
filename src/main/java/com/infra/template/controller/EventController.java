package com.infra.template.controller;

import com.infra.template.dto.EventMessage;
import com.infra.template.kafka.KafkaConsumerService;
import com.infra.template.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Kafka produce/consume demo")
public class EventController {

    private final EventService eventService;
    private final KafkaConsumerService consumerService;

    public EventController(EventService eventService, KafkaConsumerService consumerService) {
        this.eventService = eventService;
        this.consumerService = consumerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> publish(@Valid @RequestBody EventMessage event) {
        eventService.publish(event);
        return ResponseEntity.ok(Map.of("status", "published", "topic", event.topic()));
    }

    @GetMapping("/consumed")
    public ResponseEntity<List<String>> getConsumed() {
        return ResponseEntity.ok(consumerService.getConsumedMessages());
    }
}
