package com.infra.template.health;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioHealthIndicator(MinioClient minioClient, @Value("${infra.minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public Health health() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (exists) {
                return Health.up().withDetail("bucket", bucket).build();
            }
            return Health.down().withDetail("bucket", bucket).withDetail("reason", "bucket not found").build();
        } catch (Exception e) {
            return Health.down().withDetail("reason", e.getMessage()).build();
        }
    }
}
