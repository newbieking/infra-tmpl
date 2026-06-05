package com.infra.template.controller;

import com.infra.template.dto.CacheEntry;
import com.infra.template.service.CacheService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cache")
@Tag(name = "Cache", description = "Redis cache demo")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> put(@Valid @RequestBody CacheEntry entry) {
        cacheService.put(entry);
        return ResponseEntity.ok(Map.of("status", "cached", "key", entry.key()));
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String key) {
        Object value = cacheService.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String key) {
        boolean deleted = cacheService.delete(key);
        return ResponseEntity.ok(Map.of("key", key, "deleted", deleted));
    }
}
