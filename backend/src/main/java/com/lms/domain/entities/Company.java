package com.lms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 협약사 Entity
 * 교육 위탁을 위한 기업 정보를 관리
 */
@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_number", unique = true, nullable = false, length = 12)
    @NotBlank(message = "사업자등록번호는 필수입니다")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}|\\d{10}", message = "올바른 사업자등록번호 형식이어야 합니다")
    private String businessNumber;

    @Column(nullable = false)
    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 255, message = "회사명은 255자를 초과할 수 없습니다")
    private String name;

    @Column(name = "representative_name", nullable = false, length = 100)
    @NotBlank(message = "대표자명은 필수입니다")
    private String representativeName;

    @Column(length = 20)
    private String phone;

    @Column
    private String email;

    @Column(length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false)
    private ContractStatus contractStatus = ContractStatus.PENDING;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> employees = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Company(String businessNumber, String name, String representativeName,
                   String phone, String email, String address, ContractStatus contractStatus) {
        this.businessNumber = businessNumber;
        this.name = name;
        this.representativeName = representativeName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.contractStatus = contractStatus != null ? contractStatus : ContractStatus.PENDING;
    }

    /**
     * 회사 기본 정보 수정
     */
    public void updateBasicInfo(String name, String representativeName, String phone, 
                               String email, String address) {
        this.name = name;
        this.representativeName = representativeName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    /**
     * 계약 상태 변경
     */
    public void changeContractStatus(ContractStatus status) {
        this.contractStatus = status;
    }

    /**
     * 직원 추가
     */
    public void addEmployee(User user) {
        this.employees.add(user);
        user.assignCompany(this);
    }

    /**
     * 직원 제거
     */
    public void removeEmployee(User user) {
        this.employees.remove(user);
        user.assignCompany(null);
    }

    /**
     * 승인된 협약사 여부 확인
     */
    public boolean isApproved() {
        return this.contractStatus == ContractStatus.APPROVED;
    }

    /**
     * 활성 협약사 여부 확인 (직원들이 과정 신청 가능한지)
     */
    public boolean canEmployeesApplyCourses() {
        return this.contractStatus == ContractStatus.APPROVED;
    }

    /**
     * 직원 수 조회
     */
    public int getEmployeeCount() {
        return this.employees.size();
    }

    /**
     * 계약 상태 열거형
     */
    public enum ContractStatus {
        PENDING("승인대기"),
        APPROVED("승인됨"),
        REJECTED("반려됨"),
        TERMINATED("계약종료");

        private final String description;

        ContractStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}