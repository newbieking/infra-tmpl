package com.infra.template.controller;

import com.infra.template.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tx")
@Tag(name = "Transaction", description = "Seata 分布式事务演示（AT 模式）")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/order")
    @Operation(summary = "创建分布式事务订单", description = "演示 Seata @GlobalTransactional，模拟跨服务扣减库存和余额")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(transactionService.createOrder(userId, productId, quantity));
    }
}
