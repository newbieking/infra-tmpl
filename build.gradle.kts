plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.infra"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}

dependencies {
    // Web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi)

    // Data
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.data.elasticsearch)

    // Messaging
    implementation(libs.spring.kafka)

    // Object Storage
    implementation(libs.minio)

    // Microservice Infrastructure (Nacos + Sentinel + Seata)
    implementation(platform(libs.spring.cloud.bom))
    implementation(platform(libs.spring.cloud.alibaba.bom))
    implementation(libs.nacos.discovery)
    implementation(libs.nacos.config)
    implementation(libs.sentinel)
    implementation(libs.seata)

    // Observability
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.prometheus)
    implementation(libs.logstash.logback)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.kafka.test)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.elasticsearch)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.minio)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
