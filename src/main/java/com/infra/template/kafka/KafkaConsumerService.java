package com.infra.template.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final List<String> consumedMessages = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "${infra.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.debug("Received message: {}", message);
        consumedMessages.add(message);
    }

    public List<String> getConsumedMessages() {
        return List.copyOf(consumedMessages);
    }
}
