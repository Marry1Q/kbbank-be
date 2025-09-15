package com.kopo_team4.kbbank_backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("https://kbbank-be-production.up.railway.app")
                                .description("Production Server")
                ))
                .components(new Components());
    }
    
    private Info apiInfo() {
        return new Info()
                .title("국민은행 Backend API")
                .description("국민은행 백엔드 API 문서")
                .version("1.0.0");
    }
} 