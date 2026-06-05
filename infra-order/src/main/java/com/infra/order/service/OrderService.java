package com.infra.order.service;

import com.infra.order.feign.AccountClient;
import com.infra.order.feign.StockClient;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final StockClient stockClient;
    private final AccountClient accountClient;

    public OrderService(StockClient stockClient, AccountClient accountClient) {
        this.stockClient = stockClient;
        this.accountClient = accountClient;
    }

    @GlobalTransactional(name = "create-order", timeoutMills = 60000)
    public Map<String, Object> createOrder(String userId, String productId, int quantity) {
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        int amount = quantity * 100;

        log.info("[Seata AT] 创建订单: orderId={}, userId={}, productId={}, quantity={}, amount={}",
                orderId, userId, productId, quantity, amount);

        // 1. 远程调用库存服务扣减库存
        Map<String, Object> stockResult = stockClient.deductStock(productId, quantity);
        log.info("[Seata AT] 库存扣减结果: {}", stockResult);

        // 2. 远程调用账户服务扣减余额（失败会触发 Seata 全局回滚，恢复库存）
        Map<String, Object> accountResult = accountClient.deductBalance(userId, amount);
        log.info("[Seata AT] 余额扣减结果: {}", accountResult);

        // 3. 创建订单记录（本地事务）
        return Map.of(
                "orderId", orderId,
                "userId", userId,
                "productId", productId,
                "quantity", quantity,
                "amount", amount,
                "status", "CREATED"
        );
    }
}
