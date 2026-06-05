plugins {
    `java-library`
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.validation)
    api(libs.spring.boot.starter.actuator)
    api(libs.micrometer.prometheus)
    api(libs.springdoc.openapi)
    api(libs.logstash.logback)
    api(platform(libs.spring.cloud.bom))
    api(platform(libs.spring.cloud.alibaba.bom))
    api(libs.nacos.discovery)
    api(libs.loadbalancer)
    api(libs.spring.boot.starter.data.redis)
}
