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
 * 아이디 유효성 검증 UseCase 테스트
 * 
 * TDD 기반 아이디 검증 로직 테스트
 * - 형식 검증 테스트
 * - 중복 검사 테스트
 * - 금지어 검사 테스트
 * - 실시간 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("아이디 유효성 검증 UseCase 테스트")
class UsernameValidationUseCaseTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UsernameValidationUseCase usernameValidationUseCase;
    
    @BeforeEach
    void setUp() {
        // 기본적으로 중복되지 않은 상태로 설정
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
    }
    
    @Test
    @DisplayName("유효한 아이디 - 검증 성공")
    void should_ReturnValid_When_ValidUsername() {
        // Given
        String validUsername = "testuser12"; // 연속 숫자 3자리로 변경
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(validUsername);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessage()).isEqualTo("사용 가능한 아이디입니다");
        assertThat(result.getCode()).isEqualTo("VALID");
    }
    
    @Test
    @DisplayName("null 아이디 - 검증 실패")
    void should_ReturnInvalid_When_NullUsername() {
        // Given
        String nullUsername = null;
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(nullUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디를 입력해주세요");
    }
    
    @Test
    @DisplayName("빈 문자열 아이디 - 검증 실패")
    void should_ReturnInvalid_When_EmptyUsername() {
        // Given
        String emptyUsername = "   ";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(emptyUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디를 입력해주세요");
    }
    
    @Test
    @DisplayName("너무 짧은 아이디 - 검증 실패")
    void should_ReturnInvalid_When_TooShortUsername() {
        // Given
        String shortUsername = "abc";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(shortUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디는 4자 이상 입력해주세요");
    }
    
    @Test
    @DisplayName("너무 긴 아이디 - 검증 실패")
    void should_ReturnInvalid_When_TooLongUsername() {
        // Given
        String longUsername = "abcdefghijklmnopqrstuvwxyz";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(longUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디는 20자 이하로 입력해주세요");
    }
    
    @Test
    @DisplayName("숫자로 시작하는 아이디 - 검증 실패")
    void should_ReturnInvalid_When_StartsWithNumber() {
        // Given
        String numberStartUsername = "123abc";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(numberStartUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디는 영문으로 시작하고 영문, 숫자만 사용 가능합니다");
    }
    
    @Test
    @DisplayName("특수문자 포함 아이디 - 검증 실패")
    void should_ReturnInvalid_When_ContainsSpecialCharacters() {
        // Given
        String specialCharUsername = "test_user";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(specialCharUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디는 영문으로 시작하고 영문, 숫자만 사용 가능합니다");
    }
    
    @Test
    @DisplayName("연속 문자 포함 아이디 - 검증 실패")
    void should_ReturnInvalid_When_ConsecutiveCharacters() {
        // Given
        String consecutiveUsername = "testaaa";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(consecutiveUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("같은 문자를 3회 이상 연속으로 사용할 수 없습니다");
    }
    
    @Test
    @DisplayName("금지어 포함 아이디 - 검증 실패")
    void should_ReturnInvalid_When_ContainsForbiddenWord() {
        // Given
        String forbiddenUsername = "testadmin";
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(forbiddenUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("사용할 수 없는 단어가 포함되어 있습니다");
    }
    
    @Test
    @DisplayName("연속 숫자 포함 아이디 - 검증 실패")
    void should_ReturnInvalid_When_ConsecutiveNumbers() {
        // Given
        String consecutiveNumberUsername = "test1234"; // 4자리 연속 숫자
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(consecutiveNumberUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("숫자를 4자리 이상 연속으로 사용할 수 없습니다");
    }
    
    @Test
    @DisplayName("중복된 아이디 - 검증 실패")
    void should_ReturnInvalid_When_DuplicateUsername() {
        // Given
        String duplicateUsername = "testuser";
        when(userRepository.existsByUsername(duplicateUsername)).thenReturn(true);
        
        // When
        ValidationResult result = usernameValidationUseCase.validateUsername(duplicateUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("이미 사용 중인 아이디입니다");
    }
    
    @Test
    @DisplayName("실시간 검증 - 빈 값은 허용")
    void should_ReturnValid_When_QuickValidateEmptyString() {
        // Given
        String emptyUsername = "";
        
        // When
        ValidationResult result = usernameValidationUseCase.quickValidate(emptyUsername);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessage()).isEmpty();
    }
    
    @Test
    @DisplayName("실시간 검증 - 길이 초과 시 실패")
    void should_ReturnInvalid_When_QuickValidateTooLong() {
        // Given
        String longUsername = "abcdefghijklmnopqrstuvwxyz";
        
        // When
        ValidationResult result = usernameValidationUseCase.quickValidate(longUsername);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).isEqualTo("아이디는 20자 이하로 입력해주세요");
    }
    
    @Test
    @DisplayName("실시간 검증 - 유효한 입력 중 상태")
    void should_ReturnValid_When_QuickValidateValidInput() {
        // Given
        String validUsername = "testuser";
        
        // When
        ValidationResult result = usernameValidationUseCase.quickValidate(validUsername);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessage()).isEqualTo("입력 중...");
    }
    
    @Test
    @DisplayName("아이디 추천 - 기본 아이디로 추천 생성")
    void should_GenerateRecommendations_When_ValidBaseUsername() {
        // Given
        String baseUsername = "testuser";
        
        // When
        String[] recommendations = usernameValidationUseCase.generateRecommendations(baseUsername);
        
        // Then
        assertThat(recommendations).hasSize(3);
        assertThat(recommendations[0]).startsWith("testuser");
        assertThat(recommendations[1]).startsWith("testuser");
        assertThat(recommendations[2]).startsWith("testuser");
    }
    
    @Test
    @DisplayName("아이디 추천 - 너무 짧은 기본 아이디 처리")
    void should_GenerateRecommendations_When_ShortBaseUsername() {
        // Given
        String shortBaseUsername = "ab";
        
        // When
        String[] recommendations = usernameValidationUseCase.generateRecommendations(shortBaseUsername);
        
        // Then
        assertThat(recommendations).hasSize(3);
        // 짧은 아이디는 "user"가 추가되어야 함
        assertThat(recommendations[0]).startsWith("abuser");
    }
}