package com.lms;


import com.lms.domain.entities.User;
import com.lms.domain.entities.Course;
import com.lms.domain.entities.Company;
import com.lms.domain.repositories.UserRepository;
import com.lms.domain.repositories.CourseRepository;
import com.lms.domain.repositories.CompanyRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 수동으로 데이터베이스 연동 확인용 테스트
 */
@DataJpaTest
@ActiveProfiles("test")

public class ManualDatabaseTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void 실제_데이터베이스_연동_확인() {
        // 1. 회사 생성
        Company company = Company.builder()
                .businessNumber("123-45-67890")
                .name("테스트 회사")
                .representativeName("홍길동")
                .contractStatus(Company.ContractStatus.APPROVED)
                .build();
        Company savedCompany = companyRepository.save(company);
        
        // 2. 사용자 생성
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .name("테스트 사용자")
                .userType(User.UserType.STUDENT)
                .company(savedCompany)
                .isEmployee(true)
                .build();
        User savedUser = userRepository.save(user);
        
        // 3. 과정 생성
        Course course = Course.builder()
                .title("Java 프로그래밍")
                .description("Java 기초 과정")
                .durationHours(40)
                .courseType(Course.CourseType.EMPLOYEE)
                .status(Course.CourseStatus.ACTIVE)
                .build();
        Course savedCourse = courseRepository.save(course);
        
        // 4. 검증
        assertThat(savedCompany.getId()).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();
        
        // 5. 관계 검증
        assertThat(savedUser.getCompany()).isEqualTo(savedCompany);
        assertThat(savedUser.getIsEmployee()).isTrue();
        
        // 6. 비즈니스 로직 검증
        assertThat(course.canUserApply(savedUser)).isTrue(); // 재직자가 재직자 과정 신청 가능
        
        System.out.println("✅ 데이터베이스 연동 성공!");
        System.out.println("✅ 회사 ID: " + savedCompany.getId());
        System.out.println("✅ 사용자 ID: " + savedUser.getId());
        System.out.println("✅ 과정 ID: " + savedCourse.getId());
        System.out.println("✅ 생성 시간: " + savedUser.getCreatedAt());
    }
}