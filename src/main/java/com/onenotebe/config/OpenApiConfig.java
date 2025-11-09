package com.onenotebe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name:OneNote BE}") String appName) {
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version("v1")
                        .description("API documentation for " + appName));
    }
}
