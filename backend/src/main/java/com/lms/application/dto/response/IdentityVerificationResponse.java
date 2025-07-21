package com.lms.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 본인인증 응답 DTO
 * 
 * 본인인증 결과를 담는 응답 객체
 * - 인증 성공/실패 여부
 * - 인증된 사용자 정보
 * - 회원가입 진행을 위한 토큰
 */
@Getter
@Setter
@Builder
public class IdentityVerificationResponse {
    
    /**
     * 인증 성공 여부
     */
    private Boolean verified;
    
    /**
     * 인증된 사용자 이름
     */
    private String verifiedName;
    
    /**
     * 인증된 사용자 성별 (M/F)
     */
    private String gender;
    
    /**
     * 인증된 사용자 생년월일 (YYYYMMDD)
     */
    private String birthDate;
    
    /**
     * 내국인/외국인 구분 (DOMESTIC/FOREIGN)
     */
    private String nationality;
    
    /**
     * 통신사 인증 여부
     */
    private Boolean carrierVerified;
    
    /**
     * 회원가입 진행을 위한 임시 토큰 (30분 유효)
     */
    private String verificationToken;
    
    /**
     * 토큰 만료 시간
     */
    private LocalDateTime tokenExpiry;
    
    /**
     * 인증 일시
     */
    private LocalDateTime verifiedAt;
    
    /**
     * 오류 메시지 (인증 실패 시)
     */
    private String errorMessage;
    
    /**
     * 인증 제공업체 (NICE, KCB 등)
     */
    private String provider;
}