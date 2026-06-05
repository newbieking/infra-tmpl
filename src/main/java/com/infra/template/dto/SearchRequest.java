package com.infra.template.dto;

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
