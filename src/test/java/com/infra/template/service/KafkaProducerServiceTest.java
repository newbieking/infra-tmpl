package com.infra.template.service;

import com.infra.template.kafka.KafkaProducerService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendShouldCallKafkaTemplate() {
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("test-topic", 0), 0, 0, 0, 0L, 0, 0);
        SendResult<String, String> result = new SendResult<>(null, metadata);
        when(kafkaTemplate.send(eq("test-topic"), eq("key1"), eq("message")))
                .thenReturn(CompletableFuture.completedFuture(result));

        kafkaProducerService.send("test-topic", "key1", "message");

        verify(kafkaTemplate).send("test-topic", "key1", "message");
    }

    @Test
    void sendShouldHandleFailedFuture() {
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(future);

        kafkaProducerService.send("test-topic", "key1", "message");

        verify(kafkaTemplate).send("test-topic", "key1", "message");
    }
}
