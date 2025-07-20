package com.lms.infrastructure.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 설정 클래스
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.lms.domain.repositories")
@EnableTransactionManagement
public class JpaConfig {
    // JPA Auditing 활성화로 CreatedDate, LastModifiedDate 자동 처리
}