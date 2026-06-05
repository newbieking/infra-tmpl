package com.infra.order.controller;

import com.infra.common.config.GlobalExceptionHandler;
import com.infra.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrderShouldReturnOrderInfo() throws Exception {
        when(orderService.createOrder("user1", "prod1", 2))
                .thenReturn(Map.of("orderId", "abc123", "status", "CREATED"));

        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "user1")
                        .param("productId", "prod1")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("abc123"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void createOrderWhenServiceFailsShouldReturn500() throws Exception {
        when(orderService.createOrder("user1", "prod1", 1))
                .thenThrow(new RuntimeException("库存服务不可用"));

        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "user1")
                        .param("productId", "prod1"))
                .andExpect(status().isInternalServerError());
    }
}
