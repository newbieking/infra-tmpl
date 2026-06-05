package com.infra.order.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StockClientFallbackFactory implements FallbackFactory<StockClient> {

    private static final Logger log = LoggerFactory.getLogger(StockClientFallbackFactory.class);

    @Override
    public StockClient create(Throwable cause) {
        log.error("[Feign Fallback] infra-stock 服务调用失败: {}", cause.getMessage());
        return (productId, quantity) -> {
            throw new RuntimeException("库存服务不可用，请稍后重试", cause);
        };
    }
}
