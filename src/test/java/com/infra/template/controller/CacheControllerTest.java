package com.infra.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.template.dto.CacheEntry;
import com.infra.template.service.CacheService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CacheController.class)
class CacheControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CacheService cacheService;

    @BeforeEach
    void setUp(@Autowired CacheController controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void putShouldReturnCachedStatus() throws Exception {
        CacheEntry entry = new CacheEntry("mykey", "myvalue", null);

        mockMvc.perform(put("/api/v1/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cached"))
                .andExpect(jsonPath("$.key").value("mykey"));

        verify(cacheService).put(entry);
    }

    @Test
    void putWithBlankKeyShouldReturn400() throws Exception {
        String body = "{\"key\":\"\",\"value\":\"v\"}";

        mockMvc.perform(put("/api/v1/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getExistingKeyShouldReturnValue() throws Exception {
        when(cacheService.get("mykey")).thenReturn("myvalue");

        mockMvc.perform(get("/api/v1/cache/mykey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("mykey"))
                .andExpect(jsonPath("$.value").value("myvalue"));
    }

    @Test
    void getNonExistentKeyShouldReturn404() throws Exception {
        when(cacheService.get("missing")).thenReturn(null);

        mockMvc.perform(get("/api/v1/cache/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnDeletedStatus() throws Exception {
        when(cacheService.delete("mykey")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/cache/mykey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("mykey"))
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void deleteNonExistentKeyShouldReturnDeletedFalse() throws Exception {
        when(cacheService.delete("missing")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/cache/missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(false));
    }
}
