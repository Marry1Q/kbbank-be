package com.kopo_team4.hanabank_backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "국민은행 Backend API",
        version = "1.0.0",
        description = "하나은행 백엔드 API 문서"
    )
)
public class KbbankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbbankBackendApplication.class, args);
	}

}
