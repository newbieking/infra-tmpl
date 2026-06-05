package com.infra.template.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;

@Component
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.enabled", havingValue = "true", matchIfMissing = true)
public class NacosHealthIndicator implements HealthIndicator {

    private final String serverAddr;

    public NacosHealthIndicator() {
        this.serverAddr = System.getProperty("spring.cloud.nacos.discovery.server-addr",
                System.getenv().getOrDefault("NACOS_ADDR", "localhost:8848"));
    }

    @Override
    public Health health() {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create("http://" + serverAddr + "/nacos/actuator/health")
                    .toURL().openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int code = conn.getResponseCode();
            if (code == 200) {
                return Health.up().withDetail("server", serverAddr).build();
            }
            return Health.down().withDetail("server", serverAddr)
                    .withDetail("statusCode", code).build();
        } catch (Exception e) {
            return Health.down().withDetail("server", serverAddr)
                    .withDetail("reason", e.getMessage()).build();
        }
    }
}
