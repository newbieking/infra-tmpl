package com.infra.template.service;

import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @GlobalTransactional(name = "create-order", timeoutMills = 60000)
    public Map<String, Object> createOrder(String userId, String productId, int quantity) {
        String orderId = UUID.randomUUID().toString().substring(0, 8);

        log.info("[Seata AT] 创建订单: orderId={}, userId={}, productId={}, quantity={}",
                orderId, userId, productId, quantity);

        // 模拟：扣减库存
        deductStock(productId, quantity);

        // 模拟：扣减余额
        deductBalance(userId, quantity * 100);

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", userId);
        order.put("productId", productId);
        order.put("quantity", quantity);
        order.put("amount", quantity * 100);
        order.put("status", "CREATED");

        log.info("[Seata AT] 订单创建成功: {}", orderId);
        return order;
    }

    private void deductStock(String productId, int quantity) {
        log.info("[Seata AT] 扣减库存: productId={}, quantity={}", productId, quantity);
        // 实际场景：UPDATE stock SET quantity = quantity - ? WHERE product_id = ?
        // Seata AT 模式会自动拦截 SQL，生成 undo_log 用于回滚
    }

    private void deductBalance(String userId, int amount) {
        log.info("[Seata AT] 扣减余额: userId={}, amount={}", userId, amount);
        // 实际场景：UPDATE account SET balance = balance - ? WHERE user_id = ?
        // 如果此处抛出异常，Seata 会自动回滚上面的库存扣减
    }
}
