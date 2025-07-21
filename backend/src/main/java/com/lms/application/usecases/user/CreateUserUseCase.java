package com.lms.application.usecases.user;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.domain.entities.Company;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.CompanyRepository;
import com.lms.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 생성 Use Case
 * 
 * Clean Architecture의 Application Layer 구현
 * - 비즈니스 워크플로우 조정
 * - 트랜잭션 관리
 * - Domain Layer와 Infrastructure Layer 협조
 * 
 * TDD Green Phase: 테스트를 통과하는 최소한의 코드 구현
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCase {
    
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    
    /**
     * 사용자 생성 실행
     * 
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 정보
     * @throws IllegalArgumentException 중복 이메일이거나 잘못된 데이터인 경우
     */
    public UserResponse execute(CreateUserRequest request) {
        // 1. 비즈니스 규칙 검증
        validateCreateUserRequest(request);
        
        // 2. Company 엔티티 조회 (재직자인 경우)
        Company company = null;
        if (request.getIsEmployee() && request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다"));
        }
        
        // 3. User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodePassword(request.getPassword())) // 실제로는 BCrypt 암호화
                .userType(User.UserType.valueOf(request.getUserType()))
                .isEmployee(request.getIsEmployee())
                .company(company)
                .phoneNumber(request.getPhoneNumber())
                .department(request.getDepartment())
                .status(User.Status.ACTIVE)
                .build();
        
        // 4. 사용자 저장
        User savedUser = userRepository.save(user);
        
        // 5. Response DTO 변환
        return UserResponse.from(savedUser);
    }
    
    /**
     * 사용자 생성 요청 검증
     */
    private void validateCreateUserRequest(CreateUserRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }
        
        // 재직자인 경우 회사 정보 필수
        if (request.getIsEmployee() && request.getCompanyId() == null) {
            throw new IllegalArgumentException("재직자는 회사 정보가 필요합니다");
        }
        
        // 사용자 타입 검증
        try {
            User.UserType.valueOf(request.getUserType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 사용자 타입입니다");
        }
    }
    
    /**
     * 비밀번호 암호화 (Mock 구현 - 실제로는 BCryptPasswordEncoder 사용)
     */
    private String encodePassword(String rawPassword) {
        // TDD Green Phase: 최소 구현
        return "encoded_" + rawPassword;
    }
}