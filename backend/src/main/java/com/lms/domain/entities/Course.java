package com.lms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * 교육 과정 Entity
 * 재직자/구직자 대상 교육 과정 정보를 관리
 */
@Entity
@Table(name = "courses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "과정명은 필수입니다")
    @Size(max = 255, message = "과정명은 255자를 초과할 수 없습니다")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_audience", length = 100)
    private String targetAudience;

    @Column(name = "duration_hours", nullable = false)
    @NotNull(message = "교육시간은 필수입니다")
    @Min(value = 1, message = "교육시간은 1시간 이상이어야 합니다")
    private Integer durationHours;

    @Column(name = "max_participants")
    @Min(value = 1, message = "최대 참여인원은 1명 이상이어야 합니다")
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type", nullable = false)
    private CourseType courseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Course(String title, String description, String targetAudience,
                  Integer durationHours, Integer maxParticipants, 
                  CourseType courseType, CourseStatus status) {
        this.title = title;
        this.description = description;
        this.targetAudience = targetAudience;
        this.durationHours = durationHours;
        this.maxParticipants = maxParticipants;
        this.courseType = courseType;
        this.status = status != null ? status : CourseStatus.ACTIVE;
    }

    /**
     * 과정 기본 정보 수정
     */
    public void updateBasicInfo(String title, String description, String targetAudience,
                               Integer durationHours, Integer maxParticipants) {
        this.title = title;
        this.description = description;
        this.targetAudience = targetAudience;
        this.durationHours = durationHours;
        this.maxParticipants = maxParticipants;
    }

    /**
     * 과정 상태 변경
     */
    public void changeStatus(CourseStatus status) {
        this.status = status;
    }

    /**
     * 과정 활성화
     */
    public void activate() {
        this.status = CourseStatus.ACTIVE;
    }

    /**
     * 과정 비활성화
     */
    public void deactivate() {
        this.status = CourseStatus.INACTIVE;
    }

    /**
     * 활성 과정 여부 확인
     */
    public boolean isActive() {
        return this.status == CourseStatus.ACTIVE;
    }

    /**
     * 재직자 대상 과정 여부 확인
     */
    public boolean isForEmployees() {
        return this.courseType == CourseType.EMPLOYEE || this.courseType == CourseType.COMMON;
    }

    /**
     * 구직자 대상 과정 여부 확인
     */
    public boolean isForJobSeekers() {
        return this.courseType == CourseType.JOB_SEEKER || this.courseType == CourseType.COMMON;
    }

    /**
     * 특정 사용자가 신청 가능한 과정인지 확인
     */
    public boolean canUserApply(User user) {
        if (!isActive()) {
            return false;
        }
        
        if (user.getUserType() == User.UserType.STUDENT) {
            // 재직자인 경우
            if (user.isEmployee()) {
                return isForEmployees();
            }
            // 구직자인 경우
            else {
                return isForJobSeekers();
            }
        }
        
        return false;
    }

    /**
     * 과정 유형 열거형
     */
    public enum CourseType {
        EMPLOYEE("재직자"),
        JOB_SEEKER("구직자"),
        COMMON("공통");

        private final String description;

        CourseType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 과정 상태 열거형
     */
    public enum CourseStatus {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        DRAFT("임시저장");

        private final String description;

        CourseStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}