package com.infra.template.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.infra.template.dto.SearchRequest;
import com.infra.template.dto.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ElasticsearchClient esClient;

    public SearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public String index(String index, Map<String, Object> document) throws IOException {
        String id = UUID.randomUUID().toString();
        IndexResponse response = esClient.index(i -> i
                .index(index)
                .id(id)
                .document(document));
        log.debug("Indexed document {} in {}", id, index);
        return response.id();
    }

    public List<SearchResult> search(SearchRequest request) throws IOException {
        SearchResponse<Map> response = esClient.search(s -> s
                .index(request.index())
                .query(q -> q
                        .multiMatch(mm -> mm
                                .query(request.query())
                                .fields("*"))),
                Map.class);

        return response.hits().hits().stream()
                .map(this::toSearchResult)
                .toList();
    }

    private SearchResult toSearchResult(Hit<Map> hit) {
        return new SearchResult(
                hit.id(),
                hit.index(),
                hit.score() != null ? hit.score().floatValue() : 0f,
                hit.source());
    }
}
