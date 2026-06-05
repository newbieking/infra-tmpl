plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":infra-common"))
    implementation(libs.spring.kafka)
    implementation(libs.spring.boot.starter.data.redis)
    testImplementation(libs.spring.kafka.test)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(platform(libs.testcontainers.bom))
}
