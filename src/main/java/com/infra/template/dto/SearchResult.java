package com.infra.template.dto;

import java.util.Map;

public record SearchResult(
        String id,
        String index,
        float score,
        Map<String, Object> source
) {
}
