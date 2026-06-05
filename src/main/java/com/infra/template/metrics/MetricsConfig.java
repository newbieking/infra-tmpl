package com.infra.template.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter apiRequestCounter(MeterRegistry registry) {
        return Counter.builder("infra.api.requests")
                .description("Total API requests")
                .register(registry);
    }

    @Bean
    public Timer apiRequestTimer(MeterRegistry registry) {
        return Timer.builder("infra.api.duration")
                .description("API request duration")
                .register(registry);
    }
}
