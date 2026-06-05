package com.infra.search.controller;

import com.infra.common.dto.SearchRequest;
import com.infra.common.dto.SearchResult;
import com.infra.search.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "搜索服务（Elasticsearch）")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/index/{index}")
    public ResponseEntity<Map<String, String>> index(
            @PathVariable String index,
            @RequestBody Map<String, Object> document) throws Exception {
        String id = searchService.index(index, document);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "index", index));
    }

    @PostMapping
    public ResponseEntity<List<SearchResult>> search(@Valid @RequestBody SearchRequest request) throws Exception {
        return ResponseEntity.ok(searchService.search(request));
    }
}
