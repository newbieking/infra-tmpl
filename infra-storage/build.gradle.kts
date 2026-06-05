plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":infra-common"))
    implementation(libs.minio)
    testImplementation(libs.testcontainers.minio)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(platform(libs.testcontainers.bom))
}
