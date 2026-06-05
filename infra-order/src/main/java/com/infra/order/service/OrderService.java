package com.infra.order.service;

import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @GlobalTransactional(name = "create-order", timeoutMills = 60000)
    public Map<String, Object> createOrder(String userId, String productId, int quantity) {
        String orderId = UUID.randomUUID().toString().substring(0, 8);

        log.info("[Seata AT] 创建订单: orderId={}, userId={}, productId={}, quantity={}",
                orderId, userId, productId, quantity);

        deductStock(productId, quantity);
        deductBalance(userId, quantity * 100);

        return Map.of(
                "orderId", orderId,
                "userId", userId,
                "productId", productId,
                "quantity", quantity,
                "amount", quantity * 100,
                "status", "CREATED"
        );
    }

    private void deductStock(String productId, int quantity) {
        log.info("[Seata AT] 扣减库存: productId={}, quantity={}", productId, quantity);
        // TODO: 调用 infra-stock 服务的 FeignClient
    }

    private void deductBalance(String userId, int amount) {
        log.info("[Seata AT] 扣减余额: userId={}, amount={}", userId, amount);
        // TODO: 调用 infra-account 服务的 FeignClient
    }
}
