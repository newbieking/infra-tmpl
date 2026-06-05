package com.infra.template.controller;

import com.infra.template.service.FlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/flow")
@Tag(name = "FlowControl", description = "Sentinel 限流熔断降级演示")
public class FlowControlController {

    private final FlowControlService flowControlService;

    public FlowControlController(FlowControlService flowControlService) {
        this.flowControlService = flowControlService;
    }

    @GetMapping("/query")
    @Operation(summary = "受保护的资源查询", description = "使用 @SentinelResource 注解，支持限流和熔断降级")
    public ResponseEntity<Map<String, Object>> query(@RequestParam String resourceId) {
        return ResponseEntity.ok(flowControlService.queryResource(resourceId));
    }

    @GetMapping("/slow")
    @Operation(summary = "慢查询资源", description = "模拟慢查询，支持限流和熔断")
    public ResponseEntity<Map<String, Object>> slowQuery(@RequestParam String resourceId) {
        return ResponseEntity.ok(flowControlService.slowQuery(resourceId));
    }
}
