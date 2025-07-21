package com.lms.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.application.dto.request.EmployeeRegistrationRequest;
import com.lms.application.dto.request.IdentityVerificationRequest;
import com.lms.application.dto.request.JobSeekerRegistrationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.application.dto.response.RegistrationResponse;
import com.lms.application.usecases.user.EmployeeRegistrationUseCase;
import com.lms.application.usecases.user.IdentityVerificationUseCase;
import com.lms.application.usecases.user.JobSeekerRegistrationUseCase;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 회원가입 컨트롤러 테스트
 * 
 * TDD 기반 회원가입 API 테스트
 * - 본인인증 API 테스트
 * - 재직자/구직자 회원가입 API 테스트
 * - 중복 검사 API 테스트
 */
@WebMvcTest(RegistrationController.class)
@ActiveProfiles("test")
@DisplayName("회원가입 컨트롤러 테스트")
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IdentityVerificationUseCase identityVerificationUseCase;

    @MockBean
    private EmployeeRegistrationUseCase employeeRegistrationUseCase;

    @MockBean
    private JobSeekerRegistrationUseCase jobSeekerRegistrationUseCase;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("본인인증 API - 정상 요청시 200 OK 응답")
    void should_Return200OK_When_ValidIdentityVerification() throws Exception {
        // Given
        IdentityVerificationRequest request = IdentityVerificationRequest.builder()
                .name("홍길동")
                .residentNumber("901225-1234567")
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(true)
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        IdentityVerificationResponse response = IdentityVerificationResponse.builder()
                .verified(true)
                .verifiedName("홍길동")
                .gender("M")
                .birthDate("19901225")
                .nationality("DOMESTIC")
                .carrierVerified(true)
                .verificationToken("test-token-123")
                .tokenExpiry(LocalDateTime.now().plusMinutes(30))
                .verifiedAt(LocalDateTime.now())
                .provider("NICE")
                .build();

        when(identityVerificationUseCase.execute(any(IdentityVerificationRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/registration/verify-identity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("본인인증이 완료되었습니다"))
                .andExpect(jsonPath("$.data.verified").value(true))
                .andExpect(jsonPath("$.data.verifiedName").value("홍길동"))
                .andExpect(jsonPath("$.data.verificationToken").value("test-token-123"));
    }

    @Test
    @DisplayName("재직자 회원가입 API - 정상 요청시 201 Created 응답")
    void should_Return201Created_When_ValidEmployeeRegistration() throws Exception {
        // Given
        EmployeeRegistrationRequest request = EmployeeRegistrationRequest.builder()
                .verificationToken("valid-token-123")
                .username("employee123")
                .password("password123!")
                .passwordConfirm("password123!")
                .email("employee@company.com")
                .phoneNumber("010-1234-5678")
                .companyName("테스트 회사")
                .businessNumber("123-45-67890")
                .department("개발팀")
                .position("개발자")
                .supervisorName("김담당")
                .supervisorEmail("supervisor@company.com")
                .privacyAgreement(true)
                .termsAgreement(true)
                .marketingAgreement(false)
                .build();

        RegistrationResponse response = RegistrationResponse.builder()
                .success(true)
                .userId(1L)
                .username("employee123")
                .email("employee@company.com")
                .userType("EMPLOYEE")
                .accountStatus("PENDING_APPROVAL")
                .requiresApproval(true)
                .registeredAt(LocalDateTime.now())
                .canLogin(false)
                .message("재직자 회원가입이 완료되었습니다")
                .build();

        when(employeeRegistrationUseCase.execute(any(EmployeeRegistrationRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/registration/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재직자 회원가입이 완료되었습니다"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.userType").value("EMPLOYEE"))
                .andExpect(jsonPath("$.data.accountStatus").value("PENDING_APPROVAL"));
    }

    @Test
    @DisplayName("구직자 회원가입 API - 정상 요청시 201 Created 응답")
    void should_Return201Created_When_ValidJobSeekerRegistration() throws Exception {
        // Given
        JobSeekerRegistrationRequest request = JobSeekerRegistrationRequest.builder()
                .verificationToken("valid-token-123")
                .username("jobseeker123")
                .password("password123!")
                .passwordConfirm("password123!")
                .email("jobseeker@email.com")
                .phoneNumber("010-1234-5678")
                .education("UNIVERSITY")
                .careerLevel("ENTRY")
                .desiredField("웹 개발")
                .privacyAgreement(true)
                .termsAgreement(true)
                .build();

        RegistrationResponse response = RegistrationResponse.builder()
                .success(true)
                .userId(2L)
                .username("jobseeker123")
                .email("jobseeker@email.com")
                .userType("JOB_SEEKER")
                .accountStatus("ACTIVE")
                .requiresApproval(false)
                .registeredAt(LocalDateTime.now())
                .canLogin(true)
                .message("구직자 회원가입이 완료되었습니다")
                .build();

        when(jobSeekerRegistrationUseCase.execute(any(JobSeekerRegistrationRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/registration/job-seeker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("구직자 회원가입이 완료되었습니다"))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.userType").value("JOB_SEEKER"))
                .andExpect(jsonPath("$.data.accountStatus").value("ACTIVE"));
    }

    @Test
    @DisplayName("아이디 중복 검사 API - 사용 가능한 아이디인 경우 200 OK 응답")
    void should_Return200OK_When_UsernameAvailable() throws Exception {
        // Given
        String availableUsername = "available123";
        when(userRepository.existsByUsername(availableUsername)).thenReturn(false);

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
    @DisplayName("아이디 중복 검사 API - 중복된 아이디인 경우 200 OK 응답 (중복 메시지)")
    void should_Return200OK_When_UsernameDuplicate() throws Exception {
        // Given
        String duplicateUsername = "duplicate123";
        when(userRepository.existsByUsername(duplicateUsername)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/registration/check-username/{username}", duplicateUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("이메일 중복 검사 API - 사용 가능한 이메일인 경우 200 OK 응답")
    void should_Return200OK_When_EmailAvailable() throws Exception {
        // Given
        String availableEmail = "available@email.com";
        when(userRepository.existsByEmail(availableEmail)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/registration/check-email")
                        .param("email", availableEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("인증 토큰 검증 API - 유효한 토큰인 경우 200 OK 응답")
    void should_Return200OK_When_TokenValid() throws Exception {
        // Given
        String validToken = "valid-token-123";
        when(identityVerificationUseCase.validateToken(validToken)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/registration/verify-token/{token}", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유효한 인증 토큰입니다"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("본인인증 API - 필수 항목 누락시 400 Bad Request 응답")
    void should_Return400BadRequest_When_MissingRequiredFields() throws Exception {
        // Given - 이름이 누락된 요청
        IdentityVerificationRequest invalidRequest = IdentityVerificationRequest.builder()
                .residentNumber("901225-1234567")
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(true)
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/registration/verify-identity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}