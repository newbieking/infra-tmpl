package com.infra.template.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI infraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Infra Template API")
                        .description("Modern Java Infrastructure Service Blueprint")
                        .version("0.1.0")
                        .contact(new Contact().name("Infra Team")));
    }
}
