plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":infra-common"))
    implementation(libs.spring.cloud.gateway)
    implementation(libs.sentinel)
    implementation(libs.sentinel.gateway)
    implementation(libs.nacos.config)
}
