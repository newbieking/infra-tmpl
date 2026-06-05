package com.infra.template.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlowControlService {

    private static final Logger log = LoggerFactory.getLogger(FlowControlService.class);

    @SentinelResource(value = "queryResource", fallback = "queryFallback")
    public Map<String, Object> queryResource(String resourceId) {
        log.info("[Sentinel] 查询资源: {}", resourceId);
        return Map.of(
                "resourceId", resourceId,
                "data", "resource-data-" + resourceId,
                "timestamp", System.currentTimeMillis()
        );
    }

    public Map<String, Object> queryFallback(String resourceId, BlockException ex) {
        log.warn("[Sentinel] 限流/熔断降级: resourceId={}, rule={}", resourceId,
                ex != null ? ex.getRule() : "fallback");
        return Map.of(
                "resourceId", resourceId,
                "data", "降级响应：服务暂时不可用，请稍后重试",
                "degraded", true
        );
    }

    @SentinelResource(value = "slowResource",
            blockHandler = "slowBlockHandler",
            fallback = "slowFallback")
    public Map<String, Object> slowQuery(String resourceId) {
        log.info("[Sentinel] 慢查询资源: {}", resourceId);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Map.of("resourceId", resourceId, "slow", true);
    }

    public Map<String, Object> slowBlockHandler(String resourceId, BlockException ex) {
        return Map.of("resourceId", resourceId, "blocked", true, "message", "触发限流规则");
    }

    public Map<String, Object> slowFallback(String resourceId) {
        return Map.of("resourceId", resourceId, "fallback", true, "message", "服务降级");
    }
}
