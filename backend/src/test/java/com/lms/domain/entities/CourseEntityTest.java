package com.lms.domain.entities;


import com.lms.domain.repositories.CourseRepository;
import com.lms.domain.repositories.UserRepository;
import com.lms.domain.repositories.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Course Entity 테스트
 */
@DataJpaTest
@ActiveProfiles("test")

@DisplayName("Course Entity 테스트")
class CourseEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Course Entity 생성 및 저장 테스트")
    void createAndSaveCourse() {
        // Given
        Course course = Course.builder()
                .title("Java 프로그래밍 기초")
                .description("Java 언어의 기본 문법과 객체지향 프로그래밍을 학습합니다.")
                .targetAudience("프로그래밍 초보자")
                .durationHours(40)
                .maxParticipants(20)
                .courseType(Course.CourseType.COMMON)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        // When
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();

        // Then
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getTitle()).isEqualTo("Java 프로그래밍 기초");
        assertThat(savedCourse.getDescription()).contains("Java 언어의 기본 문법");
        assertThat(savedCourse.getDurationHours()).isEqualTo(40);
        assertThat(savedCourse.getMaxParticipants()).isEqualTo(20);
        assertThat(savedCourse.getCourseType()).isEqualTo(Course.CourseType.COMMON);
        assertThat(savedCourse.getStatus()).isEqualTo(Course.CourseStatus.ACTIVE);
        assertThat(savedCourse.getCreatedAt()).isNotNull();
        assertThat(savedCourse.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Course 정보 수정 테스트")
    void updateCourse() {
        // Given
        Course course = Course.builder()
                .title("수정 전 과정")
                .description("수정 전 설명")
                .durationHours(20)
                .courseType(Course.CourseType.EMPLOYEE)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();

        // When
        savedCourse.updateBasicInfo(
                "수정 후 과정",
                "수정 후 설명",
                "숙련된 개발자",
                60,
                30
        );
        courseRepository.save(savedCourse);
        entityManager.flush();

        // Then
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getTitle()).isEqualTo("수정 후 과정");
        assertThat(updatedCourse.getDescription()).isEqualTo("수정 후 설명");
        assertThat(updatedCourse.getTargetAudience()).isEqualTo("숙련된 개발자");
        assertThat(updatedCourse.getDurationHours()).isEqualTo(60);
        assertThat(updatedCourse.getMaxParticipants()).isEqualTo(30);
    }

    @Test
    @DisplayName("Course 상태 변경 테스트")
    void changeCourseStatus() {
        // Given
        Course course = Course.builder()
                .title("상태변경 테스트 과정")
                .durationHours(10)
                .courseType(Course.CourseType.COMMON)
                .status(Course.CourseStatus.DRAFT)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();

        // When
        savedCourse.activate();
        courseRepository.save(savedCourse);
        entityManager.flush();

        // Then
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getStatus()).isEqualTo(Course.CourseStatus.ACTIVE);
        assertThat(updatedCourse.isActive()).isTrue();

        // When - 비활성화
        updatedCourse.deactivate();
        courseRepository.save(updatedCourse);
        entityManager.flush();

        // Then
        Course deactivatedCourse = courseRepository.findById(savedCourse.getId()).orElse(null);
        assertThat(deactivatedCourse).isNotNull();
        assertThat(deactivatedCourse.getStatus()).isEqualTo(Course.CourseStatus.INACTIVE);
        assertThat(deactivatedCourse.isActive()).isFalse();
    }

    @Test
    @DisplayName("Course 비즈니스 메서드 테스트")
    void courseBusinessMethods() {
        // Given
        Course employeeCourse = Course.builder()
                .title("재직자 과정")
                .durationHours(20)
                .courseType(Course.CourseType.EMPLOYEE)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        Course jobSeekerCourse = Course.builder()
                .title("구직자 과정")
                .durationHours(30)
                .courseType(Course.CourseType.JOB_SEEKER)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        Course commonCourse = Course.builder()
                .title("공통 과정")
                .durationHours(25)
                .courseType(Course.CourseType.COMMON)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        // When & Then
        assertThat(employeeCourse.isForEmployees()).isTrue();
        assertThat(employeeCourse.isForJobSeekers()).isFalse();

        assertThat(jobSeekerCourse.isForEmployees()).isFalse();
        assertThat(jobSeekerCourse.isForJobSeekers()).isTrue();

        assertThat(commonCourse.isForEmployees()).isTrue();
        assertThat(commonCourse.isForJobSeekers()).isTrue();
    }

    @Test
    @DisplayName("Course 사용자 신청 가능 여부 테스트")
    void canUserApplyCourse() {
        // Given
        Company company = Company.builder()
                .businessNumber("123-45-67890")
                .name("테스트 회사")
                .representativeName("대표자")
                .contractStatus(Company.ContractStatus.APPROVED)
                .build();
        Company savedCompany = companyRepository.save(company);

        // 재직자 (회사 소속)
        User employee = User.builder()
                .username("employee")
                .password("password")
                .email("employee@test.com")
                .name("재직자")
                .userType(User.UserType.STUDENT)
                .company(savedCompany)
                .build();

        // 구직자 (회사 없음)
        User jobSeeker = User.builder()
                .username("jobseeker")
                .password("password")
                .email("jobseeker@test.com")
                .name("구직자")
                .userType(User.UserType.STUDENT)
                .build();

        userRepository.save(employee);
        userRepository.save(jobSeeker);

        Course employeeCourse = Course.builder()
                .title("재직자 전용 과정")
                .durationHours(20)
                .courseType(Course.CourseType.EMPLOYEE)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        Course jobSeekerCourse = Course.builder()
                .title("구직자 전용 과정")
                .durationHours(30)
                .courseType(Course.CourseType.JOB_SEEKER)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        Course commonCourse = Course.builder()
                .title("공통 과정")
                .durationHours(25)
                .courseType(Course.CourseType.COMMON)
                .status(Course.CourseStatus.ACTIVE)
                .build();

        // When & Then
        assertThat(employeeCourse.canUserApply(employee)).isTrue();    // 재직자가 재직자 과정 신청
        assertThat(employeeCourse.canUserApply(jobSeeker)).isFalse();  // 구직자가 재직자 과정 신청

        assertThat(jobSeekerCourse.canUserApply(employee)).isFalse();  // 재직자가 구직자 과정 신청
        assertThat(jobSeekerCourse.canUserApply(jobSeeker)).isTrue();  // 구직자가 구직자 과정 신청

        assertThat(commonCourse.canUserApply(employee)).isTrue();      // 재직자가 공통 과정 신청
        assertThat(commonCourse.canUserApply(jobSeeker)).isTrue();     // 구직자가 공통 과정 신청
    }

    @Test
    @DisplayName("Course 삭제 테스트")
    void deleteCourse() {
        // Given
        Course course = Course.builder()
                .title("삭제 테스트 과정")
                .durationHours(10)
                .courseType(Course.CourseType.COMMON)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        Long courseId = savedCourse.getId();

        // When
        courseRepository.deleteById(courseId);
        entityManager.flush();

        // Then
        assertThat(courseRepository.findById(courseId)).isEmpty();
    }
}