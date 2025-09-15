package com.kopo_team4.kbbank_backend.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
@Slf4j
public class LoggingConfig {

    @PostConstruct
    public void init() {
        // 로그 디렉토리 생성 비활성화 (파일 로깅 사용하지 않음)
        // File logDir = new File("logs");
        // if (!logDir.exists()) {
        //     boolean created = logDir.mkdirs();
        //     if (created) {
        //         log.info("로그 디렉토리 생성 완료: {}", logDir.getAbsolutePath());
        //     } else {
        //         log.warn("로그 디렉토리 생성 실패: {}", logDir.getAbsolutePath());
        //     }
        // } else {
        //     log.info("로그 디렉토리 이미 존재: {}", logDir.getAbsolutePath());
        // }
        
        log.info("=== 하나은행 백엔드 API 서버 시작 ===");
        log.info("Profile: {}", System.getProperty("spring.profiles.active", "local"));
        
        // 실제 포트 번호를 동적으로 가져오기
        String port = System.getProperty("server.port", "8080");
        log.info("Port: {}", port);
        log.info("Swagger UI: http://localhost:{}/swagger-ui.html", port);
        log.info("API Docs: http://localhost:{}/api-docs", port);
        log.info("========================================");
    }
} 