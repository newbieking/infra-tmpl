package com.infra.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.infra.common.dto.SearchRequest;
import com.infra.common.dto.SearchResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SearchService {

    private final ElasticsearchClient esClient;

    public SearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public String index(String index, Map<String, Object> document) throws IOException {
        String id = UUID.randomUUID().toString();
        IndexResponse response = esClient.index(i -> i.index(index).id(id).document(document));
        return response.id();
    }

    public List<SearchResult> search(SearchRequest request) throws IOException {
        SearchResponse<Map> response = esClient.search(s -> s
                .index(request.index())
                .query(q -> q.multiMatch(mm -> mm.query(request.query()).fields("*"))),
                Map.class);

        return response.hits().hits().stream()
                .map(hit -> new SearchResult(hit.id(), hit.index(),
                        hit.score() != null ? hit.score().floatValue() : 0f, hit.source()))
                .toList();
    }
}
