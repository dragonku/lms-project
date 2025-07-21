package com.lms.infrastructure.verification;

import com.lms.application.dto.request.IdentityVerificationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 본인인증 서비스
 * 
 * 외부 본인인증 API와 연동하여 신원을 확인하는 서비스
 * - NICE 신용정보, KCB 등 인증기관 연동
 * - 주민등록번호 검증
 * - 통신사 본인인증
 * - Mock 구현 (실제 서비스 연동 시 교체)
 */
@Service
@Slf4j
public class IdentityVerificationService {
    
    /**
     * 본인인증 실행
     * 
     * @param request 본인인증 요청 정보
     * @return 인증 결과
     */
    public IdentityVerificationResponse verifyIdentity(IdentityVerificationRequest request) {
        log.info("본인인증 요청 - 이름: {}, 휴대폰: {}", request.getName(), 
                maskPhoneNumber(request.getPhoneNumber()));
        
        // 필수 동의 항목 검증
        validateConsents(request);
        
        // Mock 본인인증 처리 (실제 환경에서는 외부 API 호출)
        IdentityVerificationResponse response = performMockVerification(request);
        
        if (response.getVerified()) {
            log.info("본인인증 성공 - 사용자: {}, 토큰: {}", 
                    response.getVerifiedName(), response.getVerificationToken());
        } else {
            log.warn("본인인증 실패 - 이름: {}, 오류: {}", 
                    request.getName(), response.getErrorMessage());
        }
        
        return response;
    }
    
    /**
     * 인증 토큰 유효성 검증
     * 
     * @param token 인증 토큰
     * @return 유효성 여부
     */
    public boolean validateVerificationToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // Mock 구현: UUID 형식 검증
        try {
            UUID.fromString(token);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 인증 토큰: {}", token);
            return false;
        }
    }
    
    /**
     * 인증 토큰으로부터 사용자 정보 추출
     * 
     * @param token 인증 토큰
     * @return 인증된 사용자 정보
     */
    public IdentityVerificationResponse getVerifiedUserInfo(String token) {
        if (!validateVerificationToken(token)) {
            return IdentityVerificationResponse.builder()
                    .verified(false)
                    .errorMessage("유효하지 않은 인증 토큰입니다")
                    .build();
        }
        
        // Mock 구현: 토큰으로부터 사용자 정보 반환
        return IdentityVerificationResponse.builder()
                .verified(true)
                .verifiedName("홍길동")
                .gender("M")
                .birthDate("19901225")
                .nationality("DOMESTIC")
                .carrierVerified(true)
                .verificationToken(token)
                .tokenExpiry(LocalDateTime.now().plusMinutes(30))
                .verifiedAt(LocalDateTime.now())
                .provider("NICE")
                .build();
    }
    
    /**
     * 필수 동의 항목 검증
     */
    private void validateConsents(IdentityVerificationRequest request) {
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
     * Mock 본인인증 처리
     * 실제 환경에서는 NICE, KCB 등 외부 API 호출
     */
    private IdentityVerificationResponse performMockVerification(IdentityVerificationRequest request) {
        try {
            // Mock 검증 로직
            String name = request.getName();
            String residentNumber = request.getResidentNumber();
            String phoneNumber = request.getPhoneNumber();
            
            // 간단한 Mock 검증 (실제로는 외부 API 호출)
            if ("홍길동".equals(name) && residentNumber.startsWith("901225")) {
                return IdentityVerificationResponse.builder()
                        .verified(true)
                        .verifiedName(name)
                        .gender(getGenderFromResidentNumber(residentNumber))
                        .birthDate(getBirthDateFromResidentNumber(residentNumber))
                        .nationality("DOMESTIC")
                        .carrierVerified(isValidCarrier(request.getCarrier()))
                        .verificationToken(generateVerificationToken())
                        .tokenExpiry(LocalDateTime.now().plusMinutes(30))
                        .verifiedAt(LocalDateTime.now())
                        .provider("NICE")
                        .build();
            } else {
                return IdentityVerificationResponse.builder()
                        .verified(false)
                        .errorMessage("입력하신 정보와 일치하는 사용자를 찾을 수 없습니다")
                        .provider("NICE")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("본인인증 처리 중 오류 발생", e);
            return IdentityVerificationResponse.builder()
                    .verified(false)
                    .errorMessage("본인인증 서비스에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요")
                    .provider("NICE")
                    .build();
        }
    }
    
    /**
     * 주민등록번호로부터 성별 추출
     */
    private String getGenderFromResidentNumber(String residentNumber) {
        if (residentNumber.length() >= 8) {
            char genderDigit = residentNumber.charAt(7);
            return (genderDigit == '1' || genderDigit == '3') ? "M" : "F";
        }
        return "M";
    }
    
    /**
     * 주민등록번호로부터 생년월일 추출
     */
    private String getBirthDateFromResidentNumber(String residentNumber) {
        if (residentNumber.length() >= 6) {
            String yyMMdd = residentNumber.substring(0, 6);
            String year = yyMMdd.substring(0, 2);
            
            // 간단한 연도 보정 (실제로는 더 정교한 로직 필요)
            String fullYear = Integer.parseInt(year) > 30 ? "19" + year : "20" + year;
            
            return fullYear + yyMMdd.substring(2);
        }
        return "19901225";
    }
    
    /**
     * 통신사 유효성 검증
     */
    private boolean isValidCarrier(String carrier) {
        return "SKT".equals(carrier) || "KT".equals(carrier) || "LG".equals(carrier);
    }
    
    /**
     * 인증 토큰 생성
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 휴대폰 번호 마스킹
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 8) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}