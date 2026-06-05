package com.infra.template.dto;

import jakarta.validation.constraints.NotBlank;

public record CacheEntry(
        @NotBlank String key,
        Object value,
        Long ttlSeconds
) {
}
