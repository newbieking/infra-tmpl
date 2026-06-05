package com.infra.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.template.dto.EventMessage;
import com.infra.template.kafka.KafkaConsumerService;
import com.infra.template.service.EventService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@ActiveProfiles("test")
class EventControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private KafkaConsumerService consumerService;

    @BeforeEach
    void setUp(@Autowired EventController controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void publishShouldReturnPublishedStatus() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"test-topic\",\"payload\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"))
                .andExpect(jsonPath("$.topic").value("test-topic"));

        verify(eventService).publish(any(EventMessage.class));
    }

    @Test
    void publishWithBlankTopicShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"\",\"payload\":\"hello\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishWithBlankPayloadShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"test-topic\",\"payload\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getConsumedShouldReturnMessageList() throws Exception {
        when(consumerService.getConsumedMessages()).thenReturn(List.of("msg1", "msg2"));

        mockMvc.perform(get("/api/v1/events/consumed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("msg1"))
                .andExpect(jsonPath("$[1]").value("msg2"));
    }

    @Test
    void getConsumedShouldReturnEmptyListWhenNoMessages() throws Exception {
        when(consumerService.getConsumedMessages()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/events/consumed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
