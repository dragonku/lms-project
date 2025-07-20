package com.lms.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers 설정 클래스
 * 
 * 실제 PostgreSQL 컨테이너를 사용한 통합 테스트 환경 구성
 * - 격리된 테스트 환경 보장
 * - 실제 데이터베이스와 동일한 환경에서 테스트
 * - Clean Architecture의 Infrastructure Layer 테스트에 사용
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("lms_test")
                .withUsername("test_user")
                .withPassword("test_password")
                .withReuse(true); // 컨테이너 재사용으로 테스트 속도 향상
    }
}