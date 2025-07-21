package com.lms.application.usecases.user;

import com.lms.application.dto.response.ValidationResult;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 간단한 아이디 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("간단한 아이디 검증 테스트")
class SimpleUsernameTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UsernameValidationUseCase usernameValidationUseCase;
    
    @BeforeEach
    void setUp() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
    }
    
    @Test
    @DisplayName("기본 검증 테스트")
    void basicTest() {
        // Given
        String username = "testuser";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(username);
        
        // Then
        System.out.println("Result: " + result.isValid());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Code: " + result.getCode());
        
        // 일단 결과만 확인
        assertThat(result).isNotNull();
    }
}