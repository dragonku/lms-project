package com.lms.application.usecases.user;

import com.lms.application.dto.request.JobSeekerRegistrationRequest;
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
import java.util.UUID;

/**
 * 구직자 회원가입 Use Case
 * 
 * Clean Architecture의 Application Layer 구현
 * - 구직자 전용 회원가입 로직
 * - 본인인증 토큰 검증
 * - 즉시 가입 승인 (별도 승인 불필요)
 * - 이메일 인증 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerRegistrationUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityVerificationUseCase identityVerificationUseCase;
    
    /**
     * 구직자 회원가입 실행
     * 
     * @param request 구직자 회원가입 요청
     * @return 가입 결과
     */
    @Transactional
    public RegistrationResponse execute(JobSeekerRegistrationRequest request) {
        log.info("구직자 회원가입 시작 - 아이디: {}, 희망직종: {}", 
                request.getUsername(), request.getDesiredField());
        
        try {
            // 1. 기본 유효성 검증
            validateRequest(request);
            
            // 2. 본인인증 토큰 검증
            IdentityVerificationResponse verificationInfo = validateVerificationToken(request.getVerificationToken());
            
            // 3. 아이디 중복 검사
            validateUsernameDuplicate(request.getUsername());
            
            // 4. 이메일 중복 검사
            validateEmailDuplicate(request.getEmail());
            
            // 5. 구직자 정보 검증
            validateJobSeekerInfo(request);
            
            // 6. User 엔티티 생성
            User user = createJobSeekerUser(request, verificationInfo);
            
            // 7. 사용자 저장
            User savedUser = userRepository.save(user);
            
            // 8. 이메일 인증 처리
            String emailVerificationToken = generateEmailVerificationToken();
            
            // 9. 환영 이메일 및 인증 메일 발송
            sendWelcomeAndVerificationEmail(savedUser, emailVerificationToken);
            
            log.info("구직자 회원가입 완료 - 사용자 ID: {}, 즉시 활성화", savedUser.getId());
            
            return RegistrationResponse.builder()
                    .success(true)
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .userType("JOB_SEEKER")
                    .accountStatus("ACTIVE")
                    .requiresApproval(false)
                    .registeredAt(LocalDateTime.now())
                    .emailVerificationRequired(true)
                    .emailVerificationToken(emailVerificationToken)
                    .canLogin(true)
                    .message("구직자 회원가입이 완료되었습니다")
                    .nextSteps("로그인 후 서비스를 이용하실 수 있습니다. 이메일 인증을 완료해주세요")
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.warn("구직자 회원가입 검증 실패: {}", e.getMessage());
            return RegistrationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("구직자 회원가입 중 예상치 못한 오류 발생", e);
            return RegistrationResponse.builder()
                    .success(false)
                    .errorMessage("회원가입 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요")
                    .build();
        }
    }
    
    /**
     * 요청 기본 유효성 검증
     */
    private void validateRequest(JobSeekerRegistrationRequest request) {
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
     * 구직자 정보 검증
     */
    private void validateJobSeekerInfo(JobSeekerRegistrationRequest request) {
        // 학력 유효성 검증
        if (!isValidEducation(request.getEducation())) {
            throw new IllegalArgumentException("올바른 학력을 선택해주세요");
        }
        
        // 경력 구분 유효성 검증
        if (!isValidCareerLevel(request.getCareerLevel())) {
            throw new IllegalArgumentException("올바른 경력 구분을 선택해주세요");
        }
        
        // 경력 개월 수 검증
        if (request.getTotalCareerMonths() != null && request.getTotalCareerMonths() < 0) {
            throw new IllegalArgumentException("경력 개월 수는 0 이상이어야 합니다");
        }
        
        // 포트폴리오 URL 검증
        if (request.getPortfolioUrl() != null && !request.getPortfolioUrl().trim().isEmpty()) {
            if (!isValidUrl(request.getPortfolioUrl())) {
                throw new IllegalArgumentException("올바른 포트폴리오 URL을 입력해주세요");
            }
        }
    }
    
    /**
     * 구직자 User 엔티티 생성
     */
    private User createJobSeekerUser(JobSeekerRegistrationRequest request, IdentityVerificationResponse verificationInfo) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(verificationInfo.getVerifiedName())
                .phoneNumber(request.getPhoneNumber())
                .userType(User.UserType.STUDENT) // 구직자도 STUDENT로 분류
                .status(User.Status.ACTIVE) // 즉시 활성화
                .isEmployee(false) // 구직자이므로 false
                .department(request.getDesiredField()) // 희망 직종을 부서 필드에 저장
                .build();
    }
    
    /**
     * 환영 이메일 및 인증 메일 발송
     */
    private void sendWelcomeAndVerificationEmail(User user, String verificationToken) {
        log.info("구직자 환영 이메일 발송 - 사용자: {}, 이메일: {}", 
                user.getUsername(), user.getEmail());
        
        // TODO: 실제 이메일 발송 구현
        // - 환영 메시지
        // - 서비스 이용 안내
        // - 이메일 인증 링크
        
        // 현재는 로그만 기록
        log.info("이메일 인증 토큰 생성 - 사용자: {}, 토큰: {}", 
                user.getUsername(), verificationToken);
    }
    
    /**
     * 이메일 인증 토큰 생성
     */
    private String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 학력 유효성 검증
     */
    private boolean isValidEducation(String education) {
        return "HIGH_SCHOOL".equals(education) || 
               "COLLEGE".equals(education) || 
               "UNIVERSITY".equals(education) || 
               "GRADUATE".equals(education);
    }
    
    /**
     * 경력 구분 유효성 검증
     */
    private boolean isValidCareerLevel(String careerLevel) {
        return "ENTRY".equals(careerLevel) || 
               "JUNIOR".equals(careerLevel) || 
               "SENIOR".equals(careerLevel) || 
               "EXPERT".equals(careerLevel);
    }
    
    /**
     * URL 유효성 검증
     */
    private boolean isValidUrl(String url) {
        if (url == null) {
            return false;
        }
        
        try {
            return url.matches("^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$");
        } catch (Exception e) {
            return false;
        }
    }
}