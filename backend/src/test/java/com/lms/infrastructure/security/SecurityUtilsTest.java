package com.lms.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Security Utils 테스트")
class SecurityUtilsTest {

    @Test
    @DisplayName("인증된 사용자의 사용자명 조회 테스트")
    void getCurrentUsername_WithAuthenticatedUser_ShouldReturnUsername() {
        // Given
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        var result = SecurityUtils.getCurrentUsername();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(username);

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 사용자명 조회 테스트")
    void getCurrentUsername_WithoutAuthentication_ShouldReturnEmpty() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        var result = SecurityUtils.getCurrentUsername();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("인증 상태 확인 테스트 - 인증된 경우")
    void isAuthenticated_WithAuthenticatedUser_ShouldReturnTrue() {
        // Given
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        boolean result = SecurityUtils.isAuthenticated();

        // Then
        assertThat(result).isTrue();

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증 상태 확인 테스트 - 인증되지 않은 경우")
    void isAuthenticated_WithoutAuthentication_ShouldReturnFalse() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        boolean result = SecurityUtils.isAuthenticated();

        // Then
        assertThat(result).isFalse();
    }
}