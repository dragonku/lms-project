package com.lms.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 재직자 회원가입 요청 DTO
 * 
 * 재직자(현직자) 전용 회원가입 양식
 * - 본인인증 토큰 필수
 * - 소속사 정보 필수
 * - 담당자 승인 필요
 */
@Getter
@Setter
@Builder
public class EmployeeRegistrationRequest {
    
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
    
    // 재직자 필수 정보
    @NotBlank(message = "회사명은 필수입니다")
    @Size(min = 2, max = 100, message = "회사명은 2-100자여야 합니다")
    private String companyName;
    
    @NotBlank(message = "사업자등록번호는 필수입니다")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "올바른 사업자등록번호 형식을 입력해주세요")
    private String businessNumber;
    
    @NotBlank(message = "부서명은 필수입니다")
    @Size(min = 2, max = 50, message = "부서명은 2-50자여야 합니다")
    private String department;
    
    @NotBlank(message = "직책은 필수입니다")
    @Size(min = 2, max = 30, message = "직책은 2-30자여야 합니다")
    private String position;
    
    /**
     * 회사 이메일 (선택사항, 있으면 인증 메일 발송)
     */
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String companyEmail;
    
    /**
     * 담당자 이름 (승인 요청용)
     */
    private String supervisorName;
    
    /**
     * 담당자 이메일 (승인 요청용)
     */
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String supervisorEmail;
    
    /**
     * 재직증명서 첨부 (선택사항)
     */
    private String employmentCertificate;
    
    /**
     * 개인정보 수집 이용 동의
     */
    private Boolean privacyAgreement;
    
    /**
     * 서비스 이용약관 동의
     */
    private Boolean termsAgreement;
    
    /**
     * 마케팅 정보 수신 동의 (선택)
     */
    private Boolean marketingAgreement;
}