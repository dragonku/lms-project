package com.lms.application.usecases.user;

import com.lms.application.dto.request.EmployeeRegistrationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.application.dto.response.RegistrationResponse;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 재직자 회원가입 Use Case 테스트
 * 
 * TDD 기반 재직자 회원가입 비즈니스 로직 검증
 * - 정상 가입 시나리오
 * - 유효성 검증 실패 시나리오
 * - 중복 검사 시나리오
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("재직자 회원가입 Use Case 테스트")
class EmployeeRegistrationUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IdentityVerificationUseCase identityVerificationUseCase;

    @InjectMocks
    private EmployeeRegistrationUseCase employeeRegistrationUseCase;

    private EmployeeRegistrationRequest validRequest;
    private IdentityVerificationResponse verificationResponse;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validRequest = EmployeeRegistrationRequest.builder()
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

        verificationResponse = IdentityVerificationResponse.builder()
                .verified(true)
                .verifiedName("홍길동")
                .gender("M")
                .birthDate("19901225")
                .nationality("DOMESTIC")
                .verificationToken("valid-token-123")
                .tokenExpiry(LocalDateTime.now().plusMinutes(30))
                .verifiedAt(LocalDateTime.now())
                .build();

        savedUser = User.builder()
                .username("employee123")
                .password("encoded-password")
                .email("employee@company.com")
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .userType(User.UserType.STUDENT)
                .status(User.Status.PENDING_APPROVAL)
                .isEmployee(true)
                .department("개발팀")
                .build();

        // Set ID using reflection for testing
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID on User entity", e);
        }
    }

    @Test
    @DisplayName("유효한 재직자 회원가입 요청시 성공 응답을 반환해야 한다")
    void should_ReturnSuccessResponse_When_ValidEmployeeRegistration() {
        // Given
        when(identityVerificationUseCase.validateToken(anyString())).thenReturn(true);
        when(identityVerificationUseCase.getVerifiedUserInfo(anyString())).thenReturn(verificationResponse);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(1L, response.getUserId());
        assertEquals("employee123", response.getUsername());
        assertEquals("employee@company.com", response.getEmail());
        assertEquals("EMPLOYEE", response.getUserType());
        assertEquals("PENDING_APPROVAL", response.getAccountStatus());
        assertTrue(response.getRequiresApproval());
        assertFalse(response.getCanLogin());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123!");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_PasswordMismatch() {
        // Given
        EmployeeRegistrationRequest invalidRequest = EmployeeRegistrationRequest.builder()
                .verificationToken("valid-token-123")
                .username("employee123")
                .password("password123!")
                .passwordConfirm("differentPassword!") // 다른 비밀번호
                .email("employee@company.com")
                .phoneNumber("010-1234-5678")
                .companyName("테스트 회사")
                .businessNumber("123-45-67890")
                .department("개발팀")
                .position("개발자")
                .privacyAgreement(true)
                .termsAgreement(true)
                .build();

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("비밀번호가 일치하지 않습니다", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("유효하지 않은 인증 토큰인 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_InvalidVerificationToken() {
        // Given
        when(identityVerificationUseCase.validateToken(anyString())).thenReturn(false);

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 아이디인 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_DuplicateUsername() {
        // Given
        when(identityVerificationUseCase.validateToken(anyString())).thenReturn(true);
        when(identityVerificationUseCase.getVerifiedUserInfo(anyString())).thenReturn(verificationResponse);
        when(userRepository.existsByUsername(anyString())).thenReturn(true); // 중복된 아이디

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("이미 사용 중인 아이디입니다", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일인 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_DuplicateEmail() {
        // Given
        when(identityVerificationUseCase.validateToken(anyString())).thenReturn(true);
        when(identityVerificationUseCase.getVerifiedUserInfo(anyString())).thenReturn(verificationResponse);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true); // 중복된 이메일

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("이미 사용 중인 이메일입니다", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("개인정보 수집 동의가 없는 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_PrivacyAgreementFalse() {
        // Given
        EmployeeRegistrationRequest invalidRequest = EmployeeRegistrationRequest.builder()
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
                .privacyAgreement(false) // 동의 안함
                .termsAgreement(true)
                .build();

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("개인정보 수집 및 이용에 동의해야 합니다", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("잘못된 사업자등록번호 형식인 경우 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_InvalidBusinessNumber() {
        // Given
        when(identityVerificationUseCase.validateToken(anyString())).thenReturn(true);
        when(identityVerificationUseCase.getVerifiedUserInfo(anyString())).thenReturn(verificationResponse);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        EmployeeRegistrationRequest invalidRequest = EmployeeRegistrationRequest.builder()
                .verificationToken("valid-token-123")
                .username("employee123")
                .password("password123!")
                .passwordConfirm("password123!")
                .email("employee@company.com")
                .phoneNumber("010-1234-5678")
                .companyName("테스트 회사")
                .businessNumber("123-45-6789") // 잘못된 형식
                .department("개발팀")
                .position("개발자")
                .privacyAgreement(true)
                .termsAgreement(true)
                .build();

        // When
        RegistrationResponse response = employeeRegistrationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("올바른 사업자등록번호를 입력해주세요", response.getErrorMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}