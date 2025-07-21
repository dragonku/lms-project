package com.lms.application.dto.response;

import com.lms.domain.entities.User;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 * 
 * Clean Architecture의 Application Layer에서 사용되는 응답 객체
 * - Domain Entity를 외부로 노출하지 않기 위한 변환 객체
 * - 클라이언트에게 필요한 정보만 선별적으로 제공
 * - Immutable 객체로 설계
 */
@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String userType;
    private String status;
    private Boolean isEmployee;
    private Long companyId;
    private String companyName;
    private String phoneNumber;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Domain Entity로부터 Response DTO 생성
     * 
     * @param user Domain Layer의 User Entity
     * @return UserResponse DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userType(user.getUserType().name())
                .status(user.getStatus().name())
                .isEmployee(user.getIsEmployee())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getName() : null)
                .phoneNumber(user.getPhoneNumber())
                .department(user.getDepartment())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}