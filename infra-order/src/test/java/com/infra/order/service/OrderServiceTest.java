package com.infra.order.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderShouldReturnOrderMap() {
        Map<String, Object> result = orderService.createOrder("user1", "prod1", 2);

        assertNotNull(result.get("orderId"));
        assertEquals("user1", result.get("userId"));
        assertEquals("prod1", result.get("productId"));
        assertEquals(2, result.get("quantity"));
        assertEquals(200, result.get("amount"));
        assertEquals("CREATED", result.get("status"));
    }
}
