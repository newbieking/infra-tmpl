package com.infra.order.service;

import com.infra.order.feign.AccountClient;
import com.infra.order.feign.StockClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private StockClient stockClient;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderShouldCallRemoteServicesAndReturnOrder() {
        when(stockClient.deductStock("prod1", 2)).thenReturn(Map.of("deducted", 2));
        when(accountClient.deductBalance("user1", 200)).thenReturn(Map.of("deducted", 200));

        Map<String, Object> result = orderService.createOrder("user1", "prod1", 2);

        assertEquals("user1", result.get("userId"));
        assertEquals("prod1", result.get("productId"));
        assertEquals(2, result.get("quantity"));
        assertEquals(200, result.get("amount"));
        assertEquals("CREATED", result.get("status"));
        assertNotNull(result.get("orderId"));

        verify(stockClient).deductStock("prod1", 2);
        verify(accountClient).deductBalance("user1", 200);
    }

    @Test
    void createOrderShouldPropagateStockServiceFailure() {
        when(stockClient.deductStock("prod1", 2)).thenThrow(new RuntimeException("库存服务不可用"));

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder("user1", "prod1", 2));

        verify(accountClient, never()).deductBalance(anyString(), anyInt());
    }

    @Test
    void createOrderShouldPropagateAccountServiceFailure() {
        when(stockClient.deductStock("prod1", 2)).thenReturn(Map.of("deducted", 2));
        when(accountClient.deductBalance("user1", 200)).thenThrow(new RuntimeException("账户服务不可用"));

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder("user1", "prod1", 2));
    }
}
