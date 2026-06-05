package com.infra.template.service;

import com.infra.template.dto.EventMessage;
import com.infra.template.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private KafkaProducerService kafkaProducer;

    @InjectMocks
    private EventService eventService;

    @Test
    void publishShouldDelegateToProducer() {
        EventMessage event = new EventMessage("test-topic", "key1", "payload", null, null);

        eventService.publish(event);

        verify(kafkaProducer).send("test-topic", "key1", "payload");
    }

    @Test
    void publishWithNullKeyShouldDelegateToProducer() {
        EventMessage event = new EventMessage("test-topic", null, "payload", null, null);

        eventService.publish(event);

        verify(kafkaProducer).send("test-topic", null, "payload");
    }
}
