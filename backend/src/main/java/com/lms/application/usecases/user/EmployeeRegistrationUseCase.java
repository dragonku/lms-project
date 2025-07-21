package com.lms.application.usecases.user;

import com.lms.application.dto.request.EmployeeRegistrationRequest;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.application.dto.response.RegistrationResponse;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 재직자 회원가입 Use Case
 * 
 * Clean Architecture의 Application Layer 구현
 * - 재직자 전용 회원가입 로직
 * - 본인인증 토큰 검증
 * - 소속사 정보 확인
 * - 담당자 승인 프로세스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeRegistrationUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityVerificationUseCase identityVerificationUseCase;
    
    /**
     * 재직자 회원가입 실행
     * 
     * @param request 재직자 회원가입 요청
     * @return 가입 결과
     */
    @Transactional
    public RegistrationResponse execute(EmployeeRegistrationRequest request) {
        log.info("재직자 회원가입 시작 - 아이디: {}, 회사: {}", 
                request.getUsername(), request.getCompanyName());
        
        try {
            // 1. 기본 유효성 검증
            validateRequest(request);
            
            // 2. 본인인증 토큰 검증
            IdentityVerificationResponse verificationInfo = validateVerificationToken(request.getVerificationToken());
            
            // 3. 아이디 중복 검사
            validateUsernameDuplicate(request.getUsername());
            
            // 4. 이메일 중복 검사
            validateEmailDuplicate(request.getEmail());
            
            // 5. 재직자 정보 검증
            validateEmployeeInfo(request);
            
            // 6. User 엔티티 생성
            User user = createEmployeeUser(request, verificationInfo);
            
            // 7. 사용자 저장
            User savedUser = userRepository.save(user);
            
            // 8. 승인 프로세스 시작 (담당자에게 승인 요청 메일 발송)
            initiateApprovalProcess(savedUser, request);
            
            log.info("재직자 회원가입 완료 - 사용자 ID: {}, 승인 대기 상태", savedUser.getId());
            
            return RegistrationResponse.builder()
                    .success(true)
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .userType("EMPLOYEE")
                    .accountStatus("PENDING_APPROVAL")
                    .requiresApproval(true)
                    .approverInfo(request.getSupervisorName() + " (" + request.getSupervisorEmail() + ")")
                    .registeredAt(LocalDateTime.now())
                    .emailVerificationRequired(false)
                    .canLogin(false)
                    .message("재직자 회원가입이 완료되었습니다")
                    .nextSteps("담당자 승인 후 로그인이 가능합니다. 승인 요청 메일이 담당자에게 발송되었습니다")
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.warn("재직자 회원가입 검증 실패: {}", e.getMessage());
            return RegistrationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("재직자 회원가입 중 예상치 못한 오류 발생", e);
            return RegistrationResponse.builder()
                    .success(false)
                    .errorMessage("회원가입 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요")
                    .build();
        }
    }
    
    /**
     * 요청 기본 유효성 검증
     */
    private void validateRequest(EmployeeRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("회원가입 요청 정보가 없습니다");
        }
        
        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // 필수 동의 항목 검증
        if (!Boolean.TRUE.equals(request.getPrivacyAgreement())) {
            throw new IllegalArgumentException("개인정보 수집 및 이용에 동의해야 합니다");
        }
        
        if (!Boolean.TRUE.equals(request.getTermsAgreement())) {
            throw new IllegalArgumentException("서비스 이용약관에 동의해야 합니다");
        }
    }
    
    /**
     * 본인인증 토큰 검증
     */
    private IdentityVerificationResponse validateVerificationToken(String token) {
        if (!identityVerificationUseCase.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요");
        }
        
        IdentityVerificationResponse verificationInfo = identityVerificationUseCase.getVerifiedUserInfo(token);
        if (!verificationInfo.getVerified()) {
            throw new IllegalArgumentException("본인인증 정보를 확인할 수 없습니다. 본인인증을 다시 진행해주세요");
        }
        
        return verificationInfo;
    }
    
    /**
     * 아이디 중복 검사
     */
    private void validateUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
        }
    }
    
    /**
     * 이메일 중복 검사
     */
    private void validateEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }
    }
    
    /**
     * 재직자 정보 검증
     */
    private void validateEmployeeInfo(EmployeeRegistrationRequest request) {
        // 사업자등록번호 형식 검증
        if (!isValidBusinessNumber(request.getBusinessNumber())) {
            throw new IllegalArgumentException("올바른 사업자등록번호를 입력해주세요");
        }
        
        // 담당자 정보 검증 (선택사항이지만 입력된 경우 유효성 검증)
        if (request.getSupervisorEmail() != null && !request.getSupervisorEmail().trim().isEmpty()) {
            if (!isValidEmail(request.getSupervisorEmail())) {
                throw new IllegalArgumentException("올바른 담당자 이메일을 입력해주세요");
            }
        }
    }
    
    /**
     * 재직자 User 엔티티 생성
     */
    private User createEmployeeUser(EmployeeRegistrationRequest request, IdentityVerificationResponse verificationInfo) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(verificationInfo.getVerifiedName())
                .phoneNumber(request.getPhoneNumber())
                .userType(User.UserType.STUDENT) // 재직자도 기본적으로 STUDENT로 분류
                .status(User.Status.PENDING_APPROVAL) // 승인 대기 상태
                .isEmployee(true)
                .department(request.getDepartment())
                .build();
    }
    
    /**
     * 승인 프로세스 시작
     */
    private void initiateApprovalProcess(User user, EmployeeRegistrationRequest request) {
        log.info("재직자 승인 프로세스 시작 - 사용자: {}, 담당자: {}", 
                user.getUsername(), request.getSupervisorEmail());
        
        // TODO: 담당자에게 승인 요청 이메일 발송
        // - 사용자 정보
        // - 회사 정보
        // - 승인/반려 링크
        
        // TODO: 승인 대기 알림 설정
        
        // 현재는 로그만 기록
        log.info("승인 요청 메일 발송 - 수신자: {}, 사용자: {}, 회사: {}", 
                request.getSupervisorEmail(), user.getName(), request.getCompanyName());
    }
    
    /**
     * 사업자등록번호 유효성 검증
     */
    private boolean isValidBusinessNumber(String businessNumber) {
        if (businessNumber == null) {
            return false;
        }
        
        // 기본 형식 검증 (XXX-XX-XXXXX)
        if (!businessNumber.matches("^\\d{3}-\\d{2}-\\d{5}$")) {
            return false;
        }
        
        // 사업자등록번호 체크섬 검증
        try {
            String numbers = businessNumber.replace("-", "");
            int[] multipliers = {1, 3, 7, 1, 3, 7, 1, 3, 5};
            int sum = 0;
            
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(numbers.charAt(i)) * multipliers[i];
            }
            
            sum += (Character.getNumericValue(numbers.charAt(8)) * 5) / 10;
            int checkDigit = (10 - (sum % 10)) % 10;
            
            return checkDigit == Character.getNumericValue(numbers.charAt(9));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 이메일 유효성 검증
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}