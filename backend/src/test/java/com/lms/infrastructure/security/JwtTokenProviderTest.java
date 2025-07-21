package com.lms.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Token Provider 테스트")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "TestSecretKeyForJwtTokenProviderTestCasesVeryLongSecretKeyForHS512Algorithm123456789";
    private final long testExpiration = 86400000; // 24시간

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(testSecret, testExpiration);
    }

    @Test
    @DisplayName("JWT 토큰 생성 테스트")
    void generateToken_ShouldGenerateValidToken() {
        // Given
        String username = "testuser";

        // When
        String token = jwtTokenProvider.generateToken(username);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자명 추출 테스트")
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        // Given
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        // When
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("유효한 JWT 토큰 검증 테스트")
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 JWT 토큰 검증 테스트")
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("빈 토큰 검증 테스트")
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // When & Then
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰 만료 시간 확인 테스트")
    void getExpirationDateFromToken_ShouldReturnCorrectDate() {
        // Given
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        // When
        var expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate.getTime()).isGreaterThan(System.currentTimeMillis());
    }
}