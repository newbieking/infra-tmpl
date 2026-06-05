package com.infra.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "infra-stock", fallbackFactory = StockClientFallbackFactory.class)
public interface StockClient {

    @PostMapping("/api/v1/stock/deduct")
    Map<String, Object> deductStock(@RequestParam String productId, @RequestParam int quantity);
}
