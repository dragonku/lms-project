package com.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * LMS (Learning Management System) 메인 애플리케이션 클래스
 * 
 * Clean Architecture를 기반으로 구현된 교육관리시스템
 * - Domain-Driven Design 적용
 * - TDD (Test-Driven Development) 방법론 사용
 * - Spring Boot 3.x + Java 21 기반
 */
@SpringBootApplication
@EnableJpaAuditing
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }
}