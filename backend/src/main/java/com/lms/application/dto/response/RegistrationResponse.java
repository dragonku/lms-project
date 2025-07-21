package com.lms.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원가입 응답 DTO
 * 
 * 회원가입 결과를 담는 공통 응답 객체
 * - 가입 성공/실패 여부
 * - 승인 상태 정보
 * - 추가 안내 메시지
 */
@Getter
@Setter
@Builder
public class RegistrationResponse {
    
    /**
     * 회원가입 성공 여부
     */
    private Boolean success;
    
    /**
     * 생성된 사용자 ID
     */
    private Long userId;
    
    /**
     * 사용자 아이디
     */
    private String username;
    
    /**
     * 사용자 이메일
     */
    private String email;
    
    /**
     * 사용자 유형 (EMPLOYEE/JOB_SEEKER)
     */
    private String userType;
    
    /**
     * 계정 상태 (PENDING_APPROVAL/ACTIVE/INACTIVE)
     */
    private String accountStatus;
    
    /**
     * 승인 필요 여부
     */
    private Boolean requiresApproval;
    
    /**
     * 승인자 정보 (재직자의 경우)
     */
    private String approverInfo;
    
    /**
     * 가입 완료 시간
     */
    private LocalDateTime registeredAt;
    
    /**
     * 이메일 인증 필요 여부
     */
    private Boolean emailVerificationRequired;
    
    /**
     * 이메일 인증 토큰 (이메일 인증 필요 시)
     */
    private String emailVerificationToken;
    
    /**
     * 추가 안내 메시지
     */
    private String message;
    
    /**
     * 다음 단계 안내
     */
    private String nextSteps;
    
    /**
     * 오류 메시지 (실패 시)
     */
    private String errorMessage;
    
    /**
     * 로그인 가능 여부
     */
    private Boolean canLogin;
}