package com.lms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 Entity
 * 관리자, 강사, 교육생, 협약사 담당자를 포함하는 통합 사용자 모델
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3-50자 사이여야 합니다")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @Column(unique = true, nullable = false)
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "is_employee", nullable = false)
    private Boolean isEmployee = false;

    @Column(length = 100)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String username, String password, String email, String name, 
                String phoneNumber, UserType userType, Status status, Company company,
                Boolean isEmployee, String department) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.status = status != null ? status : Status.ACTIVE;
        this.company = company;
        this.isEmployee = isEmployee != null ? isEmployee : false;
        this.department = department;
    }

    /**
     * 사용자 정보 수정
     */
    public void updateProfile(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 사용자 상태 변경
     */
    public void changeStatus(Status status) {
        this.status = status;
    }

    /**
     * 소속 회사 설정 (재직자의 경우)
     */
    public void assignCompany(Company company) {
        this.company = company;
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return this.userType == UserType.ADMIN;
    }

    /**
     * 강사 권한 확인
     */
    public boolean isInstructor() {
        return this.userType == UserType.INSTRUCTOR;
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    /**
     * 재직자 여부 확인
     */
    public Boolean getIsEmployee() {
        return this.isEmployee;
    }

    /**
     * 사용자 유형 열거형
     */
    public enum UserType {
        ADMIN("관리자"),
        INSTRUCTOR("강사"),
        STUDENT("교육생"),
        COMPANY_MANAGER("협약사 담당자");

        private final String description;

        UserType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 사용자 상태 열거형
     */
    public enum Status {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        PENDING("승인대기"),
        SUSPENDED("정지");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}