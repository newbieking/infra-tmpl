package com.infra.template.controller;

import com.infra.template.service.FlowControlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlowControlController.class)
@ActiveProfiles("test")
class FlowControlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlowControlService flowControlService;

    @Test
    void queryShouldReturnResourceData() throws Exception {
        when(flowControlService.queryResource("res1"))
                .thenReturn(Map.of("resourceId", "res1", "data", "test-data"));

        mockMvc.perform(get("/api/v1/flow/query").param("resourceId", "res1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value("res1"))
                .andExpect(jsonPath("$.data").value("test-data"));
    }

    @Test
    void queryShouldReturnDegradedResponse() throws Exception {
        when(flowControlService.queryResource("res1"))
                .thenReturn(Map.of("resourceId", "res1", "degraded", true));

        mockMvc.perform(get("/api/v1/flow/query").param("resourceId", "res1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degraded").value(true));
    }

    @Test
    void slowQueryShouldReturnResult() throws Exception {
        when(flowControlService.slowQuery("res1"))
                .thenReturn(Map.of("resourceId", "res1", "slow", true));

        mockMvc.perform(get("/api/v1/flow/slow").param("resourceId", "res1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slow").value(true));
    }
}
