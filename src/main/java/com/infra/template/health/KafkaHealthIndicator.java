package com.infra.template.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterResult cluster = client.describeCluster();
            int nodeCount = cluster.nodes().get().size();
            return Health.up()
                    .withDetail("nodes", nodeCount)
                    .withDetail("clusterId", cluster.clusterId().get())
                    .build();
        } catch (Exception e) {
            return Health.down().withDetail("reason", e.getMessage()).build();
        }
    }
}
