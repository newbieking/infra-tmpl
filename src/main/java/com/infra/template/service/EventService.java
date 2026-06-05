package com.infra.template.service;

import com.infra.template.dto.EventMessage;
import com.infra.template.kafka.KafkaProducerService;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final KafkaProducerService kafkaProducer;

    public EventService(KafkaProducerService kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void publish(EventMessage event) {
        kafkaProducer.send(event.topic(), event.key(), event.payload());
    }
}
