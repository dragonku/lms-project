package com.lms.application.usecases.user;

import com.lms.application.dto.request.IdentityVerificationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.infrastructure.verification.IdentityVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 본인인증 Use Case
 * 
 * Clean Architecture의 Application Layer 구현
 * - 본인인증 비즈니스 로직 조정
 * - 인증 결과 검증
 * - 보안 정책 적용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityVerificationUseCase {
    
    private final IdentityVerificationService identityVerificationService;
    
    /**
     * 본인인증 실행
     * 
     * @param request 본인인증 요청
     * @return 인증 결과
     */
    @Transactional
    public IdentityVerificationResponse execute(IdentityVerificationRequest request) {
        log.info("본인인증 Use Case 실행 - 사용자: {}", request.getName());
        
        try {
            // 1. 입력 데이터 유효성 검증
            validateRequest(request);
            
            // 2. 본인인증 서비스 호출
            IdentityVerificationResponse response = identityVerificationService.verifyIdentity(request);
            
            // 3. 인증 결과 후처리
            if (response.getVerified()) {
                log.info("본인인증 성공 - 사용자: {}, 토큰 발급", response.getVerifiedName());
                
                // 성공 시 추가 보안 검증
                validateVerificationResult(response);
            } else {
                log.warn("본인인증 실패 - 오류: {}", response.getErrorMessage());
            }
            
            return response;
            
        } catch (IllegalArgumentException e) {
            log.warn("본인인증 요청 검증 실패: {}", e.getMessage());
            return IdentityVerificationResponse.builder()
                    .verified(false)
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("본인인증 처리 중 예상치 못한 오류 발생", e);
            return IdentityVerificationResponse.builder()
                    .verified(false)
                    .errorMessage("본인인증 서비스에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")
                    .build();
        }
    }
    
    /**
     * 인증 토큰 유효성 검증
     * 
     * @param token 인증 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        return identityVerificationService.validateVerificationToken(token);
    }
    
    /**
     * 인증된 사용자 정보 조회
     * 
     * @param token 인증 토큰
     * @return 인증된 사용자 정보
     */
    public IdentityVerificationResponse getVerifiedUserInfo(String token) {
        return identityVerificationService.getVerifiedUserInfo(token);
    }
    
    /**
     * 본인인증 요청 검증
     */
    private void validateRequest(IdentityVerificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("본인인증 요청 정보가 없습니다");
        }
        
        // 이름 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해주세요");
        }
        
        // 주민등록번호 형식 검증
        if (request.getResidentNumber() == null || !isValidResidentNumber(request.getResidentNumber())) {
            throw new IllegalArgumentException("올바른 주민등록번호를 입력해주세요");
        }
        
        // 휴대폰 번호 검증
        if (request.getPhoneNumber() == null || !isValidPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("올바른 휴대폰 번호를 입력해주세요");
        }
        
        // 통신사 검증
        if (request.getCarrier() == null || !isValidCarrier(request.getCarrier())) {
            throw new IllegalArgumentException("올바른 통신사를 선택해주세요");
        }
        
        // 필수 동의 항목 검증
        if (!Boolean.TRUE.equals(request.getPrivacyAgreement())) {
            throw new IllegalArgumentException("개인정보 수집 및 이용에 동의해야 합니다");
        }
        
        if (!Boolean.TRUE.equals(request.getUniqueIdAgreement())) {
            throw new IllegalArgumentException("고유 식별 정보 수집에 동의해야 합니다");
        }
        
        if (!Boolean.TRUE.equals(request.getVerificationAgreement())) {
            throw new IllegalArgumentException("본인인증 서비스 이용에 동의해야 합니다");
        }
    }
    
    /**
     * 인증 결과 검증
     */
    private void validateVerificationResult(IdentityVerificationResponse response) {
        if (response.getVerificationToken() == null || response.getVerificationToken().trim().isEmpty()) {
            throw new IllegalStateException("인증 토큰이 생성되지 않았습니다");
        }
        
        if (response.getTokenExpiry() == null) {
            throw new IllegalStateException("토큰 만료 시간이 설정되지 않았습니다");
        }
        
        if (response.getVerifiedName() == null || response.getVerifiedName().trim().isEmpty()) {
            throw new IllegalStateException("인증된 사용자 이름이 없습니다");
        }
    }
    
    /**
     * 주민등록번호 유효성 검증
     */
    private boolean isValidResidentNumber(String residentNumber) {
        if (residentNumber == null) {
            return false;
        }
        
        // 기본 형식 검증 (YYMMDD-XXXXXXX)
        String pattern = "^\\d{6}-[1-4]\\d{6}$";
        if (!residentNumber.matches(pattern)) {
            return false;
        }
        
        // 주민등록번호 유효성 검증 로직 (체크섬 등)
        return validateResidentNumberChecksum(residentNumber);
    }
    
    /**
     * 주민등록번호 체크섬 검증
     */
    private boolean validateResidentNumberChecksum(String residentNumber) {
        try {
            // 하이픈 제거
            String numbers = residentNumber.replace("-", "");
            
            if (numbers.length() != 13) {
                return false;
            }
            
            // 체크섬 계산
            int[] multipliers = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5};
            int sum = 0;
            
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(numbers.charAt(i)) * multipliers[i];
            }
            
            int checkDigit = (11 - (sum % 11)) % 10;
            if (checkDigit >= 10) {
                checkDigit = checkDigit % 10;
            }
            
            return checkDigit == Character.getNumericValue(numbers.charAt(12));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 휴대폰 번호 유효성 검증
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        
        return phoneNumber.matches("^01[0-9]-\\d{4}-\\d{4}$");
    }
    
    /**
     * 통신사 유효성 검증
     */
    private boolean isValidCarrier(String carrier) {
        return "SKT".equals(carrier) || "KT".equals(carrier) || "LG".equals(carrier);
    }
}