package com.infra.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.template.dto.SearchRequest;
import com.infra.template.dto.SearchResult;
import com.infra.template.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@ActiveProfiles("test")
class SearchControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void setUp(@Autowired SearchController controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void indexShouldReturn201WithId() throws Exception {
        when(searchService.index(any(), any())).thenReturn("doc-abc");

        mockMvc.perform(post("/api/v1/search/index/test-index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("doc-abc"))
                .andExpect(jsonPath("$.index").value("test-index"));
    }

    @Test
    void searchShouldReturnResults() throws Exception {
        SearchResult result = new SearchResult("1", "test-index", 1.5f, Map.of("title", "hello"));
        when(searchService.search(any(SearchRequest.class))).thenReturn(List.of(result));

        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"hello\",\"index\":\"test-index\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].score").value(1.5));
    }

    @Test
    void searchWithBlankQueryShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchShouldReturnEmptyListWhenNoResults() throws Exception {
        when(searchService.search(any(SearchRequest.class))).thenReturn(List.of());

        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"nonexistent\",\"index\":\"test-index\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
