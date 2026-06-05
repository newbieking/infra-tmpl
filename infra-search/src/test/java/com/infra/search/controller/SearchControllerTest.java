package com.infra.search.controller;

import com.infra.common.config.GlobalExceptionHandler;
import com.infra.common.dto.SearchResult;
import com.infra.search.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
@Import(GlobalExceptionHandler.class)
class SearchControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void setUp(@Autowired SearchController controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();
    }

    @Test
    void indexShouldReturn201() throws Exception {
        when(searchService.index(any(), any())).thenReturn("doc-1");

        mockMvc.perform(post("/api/v1/search/index/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("doc-1"));
    }

    @Test
    void searchShouldReturnResults() throws Exception {
        when(searchService.search(any())).thenReturn(List.of(
                new SearchResult("1", "test", 1.5f, Map.of("title", "hello"))));

        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"hello\",\"index\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void searchWithBlankQueryShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
