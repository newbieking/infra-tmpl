package com.infra.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RefreshScope
public class NacosConfigService {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigService.class);

    @Value("${infra.demo.config-value:default-value}")
    private String configValue;

    @Value("${infra.demo.feature-enabled:false}")
    private boolean featureEnabled;

    @Value("${infra.demo.max-retry:3}")
    private int maxRetry;

    public Map<String, Object> getConfig() {
        log.info("[Nacos] 读取动态配置: configValue={}, featureEnabled={}, maxRetry={}",
                configValue, featureEnabled, maxRetry);
        return Map.of(
                "configValue", configValue,
                "featureEnabled", featureEnabled,
                "maxRetry", maxRetry,
                "source", "nacos-dynamic-config"
        );
    }
}
