package com.infra.template.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.infra.template.dto.SearchRequest;
import com.infra.template.dto.SearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ElasticsearchClient esClient;

    @InjectMocks
    private SearchService searchService;

    @Test
    @SuppressWarnings("unchecked")
    void indexShouldReturnDocumentId() throws IOException {
        IndexResponse response = mock(IndexResponse.class);
        when(response.id()).thenReturn("doc-123");
        doReturn(response).when(esClient).index((Function) any());

        String id = searchService.index("test-index", Map.of("title", "hello"));

        assertEquals("doc-123", id);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchShouldReturnResults() throws IOException {
        Hit<Map> hit = mock(Hit.class);
        when(hit.id()).thenReturn("1");
        when(hit.index()).thenReturn("test-index");
        when(hit.score()).thenReturn(1.5);
        when(hit.source()).thenReturn(Map.of("title", "hello"));

        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit));

        SearchResponse<Map> response = mock(SearchResponse.class);
        when(response.hits()).thenReturn(hitsMetadata);

        doReturn(response).when(esClient).search((Function) any(), any());

        SearchRequest request = new SearchRequest("hello", "test-index");
        List<SearchResult> results = searchService.search(request);

        assertEquals(1, results.size());
        assertEquals("1", results.get(0).id());
        assertEquals("test-index", results.get(0).index());
        assertEquals(1.5f, results.get(0).score());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchShouldReturnEmptyListWhenNoResults() throws IOException {
        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of());

        SearchResponse<Map> response = mock(SearchResponse.class);
        when(response.hits()).thenReturn(hitsMetadata);

        doReturn(response).when(esClient).search((Function) any(), any());

        SearchRequest request = new SearchRequest("nonexistent", "test-index");
        List<SearchResult> results = searchService.search(request);

        assertTrue(results.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchShouldHandleNullScore() throws IOException {
        Hit<Map> hit = mock(Hit.class);
        when(hit.id()).thenReturn("1");
        when(hit.index()).thenReturn("test-index");
        when(hit.score()).thenReturn(null);
        when(hit.source()).thenReturn(Map.of("title", "hello"));

        HitsMetadata<Map> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit));

        SearchResponse<Map> response = mock(SearchResponse.class);
        when(response.hits()).thenReturn(hitsMetadata);

        doReturn(response).when(esClient).search((Function) any(), any());

        SearchRequest request = new SearchRequest("hello", "test-index");
        List<SearchResult> results = searchService.search(request);

        assertEquals(0f, results.get(0).score());
    }
}
