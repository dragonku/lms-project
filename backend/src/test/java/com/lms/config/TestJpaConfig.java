package com.lms.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * 테스트용 JPA 설정
 * JPA Auditing을 명시적으로 활성화하여 @CreatedDate, @LastModifiedDate가 정상 동작하도록 함
 */
@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "testDateTimeProvider")
public class TestJpaConfig {
    
    @Bean
    public DateTimeProvider testDateTimeProvider() {
        return new DateTimeProvider() {
            @Override
            public Optional<TemporalAccessor> getNow() {
                return Optional.of(LocalDateTime.now());
            }
        };
    }
}