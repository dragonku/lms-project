package com.lms.domain.entities;


import com.lms.domain.repositories.CompanyRepository;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Entity 테스트
 * Sprint #0 User Story 2: 데이터베이스 연결 및 기본 Entity 생성 검증
 */
@DataJpaTest
@ActiveProfiles("test")

@DisplayName("User Entity 테스트")
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("User Entity 생성 및 저장 테스트")
    void createAndSaveUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .name("테스트 사용자")
                .phoneNumber("010-1234-5678")
                .userType(User.UserType.STUDENT)
                .status(User.Status.ACTIVE)
                .build();

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("테스트 사용자");
        assertThat(savedUser.getUserType()).isEqualTo(User.UserType.STUDENT);
        assertThat(savedUser.getStatus()).isEqualTo(User.Status.ACTIVE);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("User 조회 테스트 - username으로 조회")
    void findByUsername() {
        // Given
        User user = User.builder()
                .username("findtest")
                .password("password123")
                .email("findtest@example.com")
                .name("조회 테스트 사용자")
                .userType(User.UserType.ADMIN)
                .build();
        
        userRepository.save(user);
        entityManager.flush();

        // When
        User foundUser = userRepository.findByUsername("findtest").orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("findtest");
        assertThat(foundUser.getUserType()).isEqualTo(User.UserType.ADMIN);
    }

    @Test
    @DisplayName("User 수정 테스트")
    void updateUser() {
        // Given
        User user = User.builder()
                .username("updatetest")
                .password("password123")
                .email("before@example.com")
                .name("수정 전 이름")
                .userType(User.UserType.STUDENT)
                .build();
        
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // When
        savedUser.updateProfile("수정 후 이름", "010-9999-8888", "after@example.com");
        userRepository.save(savedUser);
        entityManager.flush();

        // Then
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("수정 후 이름");
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("010-9999-8888");
        assertThat(updatedUser.getEmail()).isEqualTo("after@example.com");
    }

    @Test
    @DisplayName("User 삭제 테스트")
    void deleteUser() {
        // Given
        User user = User.builder()
                .username("deletetest")
                .password("password123")
                .email("delete@example.com")
                .name("삭제 테스트 사용자")
                .userType(User.UserType.STUDENT)
                .build();
        
        User savedUser = userRepository.save(user);
        entityManager.flush();
        Long userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("User-Company 관계 테스트")
    void userCompanyRelationship() {
        // Given
        Company company = Company.builder()
                .businessNumber("123-45-67890")
                .name("테스트 회사")
                .representativeName("대표자")
                .contractStatus(Company.ContractStatus.APPROVED)
                .build();
        
        Company savedCompany = companyRepository.save(company);
        
        User employee = User.builder()
                .username("employee")
                .password("password123")
                .email("employee@company.com")
                .name("직원")
                .userType(User.UserType.STUDENT)
                .company(savedCompany)
                .isEmployee(true)
                .build();

        // When
        User savedEmployee = userRepository.save(employee);
        entityManager.flush();

        // Then
        assertThat(savedEmployee.getCompany()).isNotNull();
        assertThat(savedEmployee.getCompany().getName()).isEqualTo("테스트 회사");
        assertThat(savedEmployee.getIsEmployee()).isTrue();
    }

    @Test
    @DisplayName("User 비즈니스 메서드 테스트")
    void userBusinessMethods() {
        // Given
        User admin = User.builder()
                .username("admin")
                .password("password123")
                .email("admin@example.com")
                .name("관리자")
                .userType(User.UserType.ADMIN)
                .status(User.Status.ACTIVE)
                .build();

        User instructor = User.builder()
                .username("instructor")
                .password("password123")
                .email("instructor@example.com")
                .name("강사")
                .userType(User.UserType.INSTRUCTOR)
                .status(User.Status.INACTIVE)
                .build();

        // When & Then
        assertThat(admin.isAdmin()).isTrue();
        assertThat(admin.isInstructor()).isFalse();
        assertThat(admin.isActive()).isTrue();

        assertThat(instructor.isAdmin()).isFalse();
        assertThat(instructor.isInstructor()).isTrue();
        assertThat(instructor.isActive()).isFalse();
    }
}