package com.lms.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 구직자 회원가입 요청 DTO
 * 
 * 구직자(구직희망자) 전용 회원가입 양식
 * - 본인인증 토큰 필수
 * - 희망 분야 및 경력 정보
 * - 즉시 가입 승인
 */
@Getter
@Setter
@Builder
public class JobSeekerRegistrationRequest {
    
    @NotBlank(message = "본인인증 토큰은 필수입니다")
    private String verificationToken;
    
    @NotBlank(message = "아이디는 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 4-20자의 영문, 숫자만 입력 가능합니다")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", 
             message = "비밀번호는 8-20자의 영문, 숫자, 특수문자를 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;
    
    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$", message = "올바른 휴대폰 번호 형식을 입력해주세요")
    private String phoneNumber;
    
    // 구직자 관련 정보
    @NotBlank(message = "학력은 필수입니다")
    private String education; // HIGH_SCHOOL, COLLEGE, UNIVERSITY, GRADUATE
    
    @Size(max = 100, message = "학교명은 100자 이하여야 합니다")
    private String schoolName;
    
    @Size(max = 50, message = "전공은 50자 이하여야 합니다")
    private String major;
    
    @NotBlank(message = "경력 구분은 필수입니다")
    private String careerLevel; // ENTRY, JUNIOR, SENIOR, EXPERT
    
    /**
     * 총 경력 (개월 수)
     */
    private Integer totalCareerMonths;
    
    @NotBlank(message = "희망 직종은 필수입니다")
    @Size(min = 2, max = 50, message = "희망 직종은 2-50자여야 합니다")
    private String desiredField;
    
    @Size(max = 100, message = "희망 지역은 100자 이하여야 합니다")
    private String desiredLocation;
    
    /**
     * 이전 직장 정보 (선택사항)
     */
    @Size(max = 100, message = "이전 직장명은 100자 이하여야 합니다")
    private String previousCompany;
    
    @Size(max = 50, message = "이전 직책은 50자 이하여야 합니다")
    private String previousPosition;
    
    /**
     * 자기소개 (선택사항)
     */
    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다")
    private String introduction;
    
    /**
     * 포트폴리오 URL (선택사항)
     */
    private String portfolioUrl;
    
    /**
     * 개인정보 수집 이용 동의
     */
    private Boolean privacyAgreement;
    
    /**
     * 서비스 이용약관 동의
     */
    private Boolean termsAgreement;
    
    /**
     * 구직 정보 제공 동의 (선택)
     */
    private Boolean jobInfoAgreement;
    
    /**
     * 마케팅 정보 수신 동의 (선택)
     */
    private Boolean marketingAgreement;
}