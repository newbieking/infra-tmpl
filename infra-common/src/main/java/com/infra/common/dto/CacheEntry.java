package com.infra.common.dto;

import jakarta.validation.constraints.NotBlank;

public record CacheEntry(
        @NotBlank String key,
        Object value,
        Long ttlSeconds
) {
}
