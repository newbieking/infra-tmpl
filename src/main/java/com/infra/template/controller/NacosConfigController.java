package com.infra.template.controller;

import com.infra.template.service.NacosConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
@Tag(name = "Config", description = "Nacos 配置中心动态刷新演示")
public class NacosConfigController {

    private final NacosConfigService nacosConfigService;

    public NacosConfigController(NacosConfigService nacosConfigService) {
        this.nacosConfigService = nacosConfigService;
    }

    @GetMapping
    @Operation(summary = "获取动态配置", description = "读取 Nacos 下发的配置，支持 @RefreshScope 热更新")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(nacosConfigService.getConfig());
    }
}
