package com.lms.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 본인인증 요청 DTO
 * 
 * 신원 확인을 위한 기본 정보를 담는 데이터 전송 객체
 * - 주민등록번호를 통한 본인인증
 * - 통신사 인증 지원
 * - 개인정보 수집 동의 포함
 */
@Getter
@Setter
@Builder
public class IdentityVerificationRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]{2,30}$", message = "이름은 2-30자의 한글, 영문만 입력 가능합니다")
    private String name;
    
    @NotBlank(message = "주민등록번호는 필수입니다")
    @Pattern(regexp = "^\\d{6}-[1-4]\\d{6}$", message = "올바른 주민등록번호 형식을 입력해주세요")
    private String residentNumber;
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$", message = "올바른 휴대폰 번호 형식을 입력해주세요")
    private String phoneNumber;
    
    @NotBlank(message = "통신사는 필수입니다")
    private String carrier; // SKT, KT, LG
    
    /**
     * 개인정보 수집 및 이용 동의
     */
    private Boolean privacyAgreement;
    
    /**
     * 고유 식별 정보 수집 동의 (주민등록번호)
     */
    private Boolean uniqueIdAgreement;
    
    /**
     * 본인인증 서비스 이용 동의
     */
    private Boolean verificationAgreement;
}