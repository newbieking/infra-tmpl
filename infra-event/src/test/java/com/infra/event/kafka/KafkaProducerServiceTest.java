package com.infra.event.kafka;

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
                new TopicPartition("topic1", 0), 0, 0, 0, 0L, 0, 0);
        when(kafkaTemplate.send(eq("topic1"), eq("key1"), eq("msg")))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, metadata)));

        kafkaProducerService.send("topic1", "key1", "msg");
        verify(kafkaTemplate).send("topic1", "key1", "msg");
    }

    @Test
    void sendShouldHandleFailedFuture() {
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        kafkaProducerService.send("topic1", "key1", "msg");
        verify(kafkaTemplate).send("topic1", "key1", "msg");
    }
}
