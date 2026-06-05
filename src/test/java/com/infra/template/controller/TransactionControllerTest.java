package com.infra.template.controller;

import com.infra.template.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createOrderShouldReturnOrderInfo() throws Exception {
        when(transactionService.createOrder("user1", "prod1", 2))
                .thenReturn(Map.of("orderId", "abc123", "status", "CREATED"));

        mockMvc.perform(post("/api/v1/tx/order")
                        .param("userId", "user1")
                        .param("productId", "prod1")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("abc123"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}
