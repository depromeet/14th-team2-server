package com.praise.push.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info=@Info(title="Praise Up API",
        description="Praise Up API v1.0",
        version="v1")
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi swaggerApi() {
        String [] paths = {"/*", "/**", "/api/**"};

        return GroupedOpenApi.builder()
            .group("Praise Up API v1.0")
            .pathsToMatch(paths)
            .build();
    }
}
