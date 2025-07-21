package com.lms.application.usecases.user;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.config.TddTestConfig;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.CompanyRepository;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * CreateUserUseCase TDD 테스트
 * 
 * Red-Green-Refactor 사이클을 따라 구현:
 * 1. Red: 실패하는 테스트 작성
 * 2. Green: 테스트를 통과하는 최소한의 코드 작성
 * 3. Refactor: 코드 개선 및 리팩토링
 */
@ExtendWith(MockitoExtension.class)
@Import(TddTestConfig.class)
@ActiveProfiles("test")
@DisplayName("사용자 생성 UseCase TDD 테스트")
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    private CreateUserUseCase createUserUseCase;
    
    @BeforeEach
    void setUp() {
        // Given: CreateUserUseCase 초기화 (아직 구현되지 않음 - RED 단계)
        createUserUseCase = new CreateUserUseCase(userRepository, companyRepository);
    }
    
    @Test
    @DisplayName("RED: 관리자 사용자 생성 요청시 UserResponse를 반환해야 한다")
    void should_ReturnUserResponse_When_CreateAdminUser() {
        // Given: 관리자 사용자 생성 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("admin")
                .email("admin@lms.com")
                .password("password123")
                .userType("ADMIN")
                .isEmployee(false)
                .build();
        
        User savedUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@lms.com")
                .userType(User.UserType.ADMIN)
                .isEmployee(false)
                .status(User.Status.ACTIVE)
                .build();
        
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        
        // When: 사용자 생성 실행 (현재는 컴파일 에러 발생할 예정)
        UserResponse response = createUserUseCase.execute(request);
        
        // Then: 응답 검증
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getEmail()).isEqualTo("admin@lms.com");
        assertThat(response.getUserType()).isEqualTo("ADMIN");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        
        // 저장소 호출 검증
        then(userRepository).should().save(any(User.class));
    }
    
    @Test
    @DisplayName("RED: 재직자 사용자 생성시 회사 정보를 포함해야 한다")
    void should_IncludeCompanyInfo_When_CreateEmployeeUser() {
        // Given: 재직자 사용자 생성 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("employee1")
                .email("employee1@company.com")
                .password("password123")
                .userType("STUDENT")
                .isEmployee(true)
                .companyId(1L)
                .build();
        
        // When & Then: 구현되지 않은 상태에서 테스트 실행
        assertThatThrownBy(() -> createUserUseCase.execute(request))
                .isInstanceOf(RuntimeException.class);
    }
    
    @Test
    @DisplayName("RED: 중복된 이메일로 사용자 생성시 예외가 발생해야 한다")
    void should_ThrowException_When_DuplicateEmail() {
        // Given: 중복 이메일 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user1")
                .email("duplicate@lms.com")
                .password("password123")
                .userType("STUDENT")
                .isEmployee(false)
                .build();
        
        given(userRepository.existsByEmail("duplicate@lms.com")).willReturn(true);
        
        // When & Then: 중복 이메일 예외 검증
        assertThatThrownBy(() -> createUserUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 이메일입니다");
    }
}