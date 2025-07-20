package com.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정
 * Sprint #0 User Story 2: 데이터베이스 연결 및 기본 Entity 생성
 * - JPA Auditing 활성화 (@CreatedDate, @LastModifiedDate 자동 설정)
 * - JPA Repository 스캔 설정
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.lms.domain.repositories")
public class JpaConfig {
    // JPA Auditing을 통해 @CreatedDate, @LastModifiedDate 자동 관리
    // 별도의 AuditorAware 설정 없이 타임스탬프만 관리
}