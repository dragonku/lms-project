package com.lms.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.application.dto.response.ValidationResult;
import com.lms.application.usecases.user.EmployeeRegistrationUseCase;
import com.lms.application.usecases.user.IdentityVerificationUseCase;
import com.lms.application.usecases.user.JobSeekerRegistrationUseCase;
import com.lms.application.usecases.user.UsernameValidationUseCase;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 아이디 검증 컨트롤러 테스트
 * 
 * User Story 2: 아이디 중복 검사 및 유효성 검증 테스트
 * - 종합 검증 API 테스트
 * - 실시간 검증 API 테스트
 * - 추천 API 테스트
 */
@WebMvcTest(RegistrationController.class)
@ActiveProfiles("test")
@DisplayName("아이디 검증 컨트롤러 테스트")
class UsernameValidationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UsernameValidationUseCase usernameValidationUseCase;
    
    @MockBean
    private IdentityVerificationUseCase identityVerificationUseCase;
    
    @MockBean
    private EmployeeRegistrationUseCase employeeRegistrationUseCase;
    
    @MockBean
    private JobSeekerRegistrationUseCase jobSeekerRegistrationUseCase;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    @DisplayName("아이디 종합 검증 API - 유효한 아이디인 경우 200 OK 응답")
    void should_Return200OK_When_ValidUsernameValidation() throws Exception {
        // Given
        String validUsername = "testuser123";
        ValidationResult validResult = ValidationResult.valid("사용 가능한 아이디입니다");
        
        when(usernameValidationUseCase.validateUsername(validUsername))
                .thenReturn(validResult);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/validate-username/{username}", validUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다"))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.code").value("VALID"));
    }
    
    @Test
    @DisplayName("아이디 종합 검증 API - 무효한 아이디인 경우 200 OK 응답 (에러 메시지)")
    void should_Return200OK_When_InvalidUsernameValidation() throws Exception {
        // Given
        String invalidUsername = "ab";
        ValidationResult invalidResult = ValidationResult.invalid("아이디는 4자 이상 입력해주세요");
        
        when(usernameValidationUseCase.validateUsername(invalidUsername))
                .thenReturn(invalidResult);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/validate-username/{username}", invalidUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디는 4자 이상 입력해주세요"));
    }
    
    @Test
    @DisplayName("실시간 아이디 검증 API - 타이핑 중 상태인 경우 200 OK 응답")
    void should_Return200OK_When_QuickValidateTyping() throws Exception {
        // Given
        String typingUsername = "test";
        ValidationResult typingResult = ValidationResult.valid("입력 중...");
        
        when(usernameValidationUseCase.quickValidate(typingUsername))
                .thenReturn(typingResult);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/quick-validate-username")
                        .param("username", typingUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("입력 중..."))
                .andExpect(jsonPath("$.data.valid").value(true));
    }
    
    @Test
    @DisplayName("실시간 아이디 검증 API - 길이 초과인 경우 200 OK 응답 (에러 메시지)")
    void should_Return200OK_When_QuickValidateTooLong() throws Exception {
        // Given
        String longUsername = "abcdefghijklmnopqrstuvwxyz";
        ValidationResult longResult = ValidationResult.invalid("아이디는 20자 이하로 입력해주세요");
        
        when(usernameValidationUseCase.quickValidate(longUsername))
                .thenReturn(longResult);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/quick-validate-username")
                        .param("username", longUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디는 20자 이하로 입력해주세요"));
    }
    
    @Test
    @DisplayName("아이디 추천 API - 추천 목록 반환")
    void should_Return200OK_When_RecommendUsername() throws Exception {
        // Given
        String baseUsername = "testuser";
        String[] recommendations = {"testuser01", "testuser02", "testuser03"};
        
        when(usernameValidationUseCase.generateRecommendations(baseUsername))
                .thenReturn(recommendations);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/recommend-username")
                        .param("baseUsername", baseUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("추천 아이디 목록입니다"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("testuser01"))
                .andExpect(jsonPath("$.data[1]").value("testuser02"))
                .andExpect(jsonPath("$.data[2]").value("testuser03"));
    }
    
    @Test
    @DisplayName("기존 아이디 중복 검사 API - 호환성 테스트")
    void should_Return200OK_When_CheckUsernameDuplicate() throws Exception {
        // Given
        String availableUsername = "available123";
        ValidationResult validResult = ValidationResult.valid("사용 가능한 아이디입니다");
        
        when(usernameValidationUseCase.validateUsername(availableUsername))
                .thenReturn(validResult);
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/check-username/{username}", availableUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다"))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    @DisplayName("아이디 검증 오류 처리 - 서비스 오류 시 500 응답")
    void should_Return500_When_ValidationServiceError() throws Exception {
        // Given
        String username = "testuser";
        when(usernameValidationUseCase.validateUsername(anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));
        
        // When & Then
        mockMvc.perform(get("/api/v1/registration/validate-username/{username}", username))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디 검증 중 오류가 발생했습니다"));
    }
    
    @Test
    @DisplayName("실시간 검증 성능 테스트 - 빠른 응답 확인")
    void should_RespondQuickly_When_QuickValidate() throws Exception {
        // Given
        String username = "test";
        ValidationResult result = ValidationResult.valid("입력 중...");
        
        when(usernameValidationUseCase.quickValidate(username))
                .thenReturn(result);
        
        // When & Then
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/v1/registration/quick-validate-username")
                        .param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        // 실시간 검증은 100ms 이내 응답해야 함
        assert responseTime < 100;
    }
}