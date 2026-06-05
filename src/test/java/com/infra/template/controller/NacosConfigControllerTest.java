package com.infra.template.controller;

import com.infra.template.service.NacosConfigService;
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

@WebMvcTest(NacosConfigController.class)
@ActiveProfiles("test")
class NacosConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NacosConfigService nacosConfigService;

    @Test
    void getConfigShouldReturnDynamicConfig() throws Exception {
        when(nacosConfigService.getConfig())
                .thenReturn(Map.of("configValue", "test-val", "featureEnabled", true, "maxRetry", 5));

        mockMvc.perform(get("/api/v1/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configValue").value("test-val"))
                .andExpect(jsonPath("$.featureEnabled").value(true))
                .andExpect(jsonPath("$.maxRetry").value(5));
    }
}
