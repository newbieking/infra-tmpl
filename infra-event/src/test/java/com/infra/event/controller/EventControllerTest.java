package com.infra.event.controller;

import com.infra.common.config.GlobalExceptionHandler;
import com.infra.common.dto.CacheEntry;
import com.infra.common.dto.EventMessage;
import com.infra.event.kafka.KafkaConsumerService;
import com.infra.event.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private KafkaConsumerService consumerService;

    @Test
    void publishShouldReturnPublished() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"t1\",\"payload\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));
        verify(eventService).publishEvent(any(EventMessage.class));
    }

    @Test
    void getConsumedShouldReturnMessages() throws Exception {
        when(consumerService.getConsumedMessages()).thenReturn(List.of("msg1", "msg2"));

        mockMvc.perform(get("/api/v1/events/consumed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("msg1"))
                .andExpect(jsonPath("$[1]").value("msg2"));
    }

    @Test
    void putCacheShouldReturnCached() throws Exception {
        mockMvc.perform(put("/api/v1/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"k1\",\"value\":\"v1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("k1"));
        verify(eventService).putCache(any(CacheEntry.class));
    }

    @Test
    void getCacheShouldReturnValue() throws Exception {
        when(eventService.getCache("k1")).thenReturn("v1");

        mockMvc.perform(get("/api/v1/cache/k1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("v1"));
    }

    @Test
    void getCacheNonExistentShouldReturn404() throws Exception {
        when(eventService.getCache("missing")).thenReturn(null);

        mockMvc.perform(get("/api/v1/cache/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCacheShouldReturnDeleted() throws Exception {
        when(eventService.deleteCache("k1")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/cache/k1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }
}
