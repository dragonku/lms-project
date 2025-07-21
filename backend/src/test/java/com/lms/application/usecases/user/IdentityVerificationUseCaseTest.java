package com.lms.application.usecases.user;

import com.lms.application.dto.request.IdentityVerificationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.infrastructure.verification.IdentityVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 본인인증 Use Case 테스트
 * 
 * TDD 기반 본인인증 비즈니스 로직 검증
 * - 정상 인증 시나리오
 * - 유효성 검증 실패 시나리오
 * - 예외 처리 시나리오
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("본인인증 Use Case 테스트")
class IdentityVerificationUseCaseTest {

    @Mock
    private IdentityVerificationService identityVerificationService;

    @InjectMocks
    private IdentityVerificationUseCase identityVerificationUseCase;

    private IdentityVerificationRequest validRequest;
    private IdentityVerificationResponse successResponse;

    @BeforeEach
    void setUp() {
        validRequest = IdentityVerificationRequest.builder()
                .name("홍길동")
                .residentNumber("901225-1234567")
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(true)
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        successResponse = IdentityVerificationResponse.builder()
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
    }

    @Test
    @DisplayName("정상적인 본인인증 요청시 성공 응답을 반환해야 한다")
    void should_ReturnSuccessResponse_When_ValidVerificationRequest() {
        // Given
        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
                .thenReturn(successResponse);

        // When
        IdentityVerificationResponse response = identityVerificationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertTrue(response.getVerified());
        assertEquals("홍길동", response.getVerifiedName());
        assertEquals("test-token-123", response.getVerificationToken());
        assertNotNull(response.getTokenExpiry());
        
        verify(identityVerificationService, times(1)).verifyIdentity(validRequest);
    }

    @Test
    @DisplayName("이름이 없는 경우 검증 실패 응답을 반환해야 한다")
    void should_ReturnValidationError_When_NameIsEmpty() {
        // Given
        IdentityVerificationRequest invalidRequest = IdentityVerificationRequest.builder()
                .name("")
                .residentNumber("901225-1234567")
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(true)
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        // When
        IdentityVerificationResponse response = identityVerificationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getVerified());
        assertEquals("이름을 입력해주세요", response.getErrorMessage());
        
        verify(identityVerificationService, never()).verifyIdentity(any());
    }

    @Test
    @DisplayName("잘못된 주민등록번호 형식인 경우 검증 실패 응답을 반환해야 한다")
    void should_ReturnValidationError_When_InvalidResidentNumber() {
        // Given
        IdentityVerificationRequest invalidRequest = IdentityVerificationRequest.builder()
                .name("홍길동")
                .residentNumber("901225-123456") // 잘못된 형식
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(true)
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        // When
        IdentityVerificationResponse response = identityVerificationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getVerified());
        assertEquals("올바른 주민등록번호를 입력해주세요", response.getErrorMessage());
        
        verify(identityVerificationService, never()).verifyIdentity(any());
    }

    @Test
    @DisplayName("개인정보 수집 동의가 없는 경우 검증 실패 응답을 반환해야 한다")
    void should_ReturnValidationError_When_PrivacyAgreementFalse() {
        // Given
        IdentityVerificationRequest invalidRequest = IdentityVerificationRequest.builder()
                .name("홍길동")
                .residentNumber("901225-1234567")
                .phoneNumber("010-1234-5678")
                .carrier("SKT")
                .privacyAgreement(false) // 동의 안함
                .uniqueIdAgreement(true)
                .verificationAgreement(true)
                .build();

        // When
        IdentityVerificationResponse response = identityVerificationUseCase.execute(invalidRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getVerified());
        assertEquals("개인정보 수집 및 이용에 동의해야 합니다", response.getErrorMessage());
        
        verify(identityVerificationService, never()).verifyIdentity(any());
    }

    @Test
    @DisplayName("유효한 토큰에 대해 true를 반환해야 한다")
    void should_ReturnTrue_When_ValidToken() {
        // Given
        String validToken = "valid-token-123";
        when(identityVerificationService.validateVerificationToken(validToken)).thenReturn(true);

        // When
        boolean result = identityVerificationUseCase.validateToken(validToken);

        // Then
        assertTrue(result);
        verify(identityVerificationService, times(1)).validateVerificationToken(validToken);
    }

    @Test
    @DisplayName("무효한 토큰에 대해 false를 반환해야 한다")
    void should_ReturnFalse_When_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";
        when(identityVerificationService.validateVerificationToken(invalidToken)).thenReturn(false);

        // When
        boolean result = identityVerificationUseCase.validateToken(invalidToken);

        // Then
        assertFalse(result);
        verify(identityVerificationService, times(1)).validateVerificationToken(invalidToken);
    }

    @Test
    @DisplayName("서비스에서 예외 발생시 실패 응답을 반환해야 한다")
    void should_ReturnErrorResponse_When_ServiceThrowsException() {
        // Given
        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
                .thenThrow(new RuntimeException("서비스 오류"));

        // When
        IdentityVerificationResponse response = identityVerificationUseCase.execute(validRequest);

        // Then
        assertNotNull(response);
        assertFalse(response.getVerified());
        assertEquals("본인인증 서비스에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요", response.getErrorMessage());
        
        verify(identityVerificationService, times(1)).verifyIdentity(validRequest);
    }
}