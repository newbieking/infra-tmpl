package com.infra.common.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank String query,
        String index
) {
    public SearchRequest {
        if (index == null || index.isBlank()) {
            index = "documents";
        }
    }
}
