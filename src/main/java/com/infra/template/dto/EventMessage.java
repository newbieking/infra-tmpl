package com.infra.template.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;

public record EventMessage(
        @NotBlank String topic,
        String key,
        @NotBlank String payload,
        Map<String, String> headers,
        Instant timestamp
) {
    public EventMessage {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
