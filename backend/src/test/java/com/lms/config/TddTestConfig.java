package com.lms.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TDD 전용 테스트 설정 클래스
 * 
 * TDD Red-Green-Refactor 사이클을 지원하는 테스트 환경 구성
 * - 예측 가능한 시간 제공 (JPA Auditing용)
 * - 테스트 격리 보장
 * - Mock 객체 관리
 */
@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "tddDateTimeProvider")
@Profile("test")
public class TddTestConfig {

    /**
     * TDD 테스트용 고정 시간 제공자
     * 테스트의 예측 가능성을 위해 고정된 시간 반환
     */
    @Bean
    @Primary
    public DateTimeProvider tddDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.of(2023, 11, 20, 10, 30, 0));
    }
}