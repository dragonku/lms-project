package com.lms.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 사용자 생성 요청 DTO
 * 
 * Clean Architecture의 Application Layer에서 사용되는 데이터 전송 객체
 * - 외부 계층(Presentation)에서 내부 계층(Domain)으로 데이터 전달
 * - 유효성 검증 어노테이션 포함
 * - Immutable 객체로 설계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3-50자여야 합니다")
    private String username;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자여야 합니다")
    private String password;
    
    @NotBlank(message = "사용자 타입은 필수입니다")
    private String userType;
    
    @NotNull(message = "재직자 여부는 필수입니다")
    private Boolean isEmployee;
    
    /**
     * 재직자인 경우 회사 ID (선택적)
     */
    private Long companyId;
    
    /**
     * 전화번호 (선택적)
     */
    private String phoneNumber;
    
    /**
     * 부서 (선택적)
     */
    private String department;
}