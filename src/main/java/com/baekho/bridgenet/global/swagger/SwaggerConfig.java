package com.baekho.bridgenet.global.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bridgenet",
                version = "v1",
                description = "Bridgenet (CrossChain Bridge Service) API"
        )
)
public class SwaggerConfig {
}
