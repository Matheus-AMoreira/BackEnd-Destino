package com.fatec.destino.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(Info().title("Destino API").version("1.0"))
            .servers(listOf(
                // Priorize a porta 8080 se for a que você está usando no momento
                Server().url("http://localhost:8080").description("Servidor Local"),
                Server().url("https://localhost:8443").description("Servidor Seguro")
            ))
    }
}