package com.lms.presentation.controllers;

import com.lms.application.dto.request.EmployeeRegistrationRequest;
import com.lms.application.dto.request.IdentityVerificationRequest;
import com.lms.application.dto.request.JobSeekerRegistrationRequest;
import com.lms.application.dto.response.ApiResponse;
import com.lms.application.dto.response.IdentityVerificationResponse;
import com.lms.application.dto.response.RegistrationResponse;
import com.lms.application.dto.response.ValidationResult;
import com.lms.application.usecases.user.EmployeeRegistrationUseCase;
import com.lms.application.usecases.user.IdentityVerificationUseCase;
import com.lms.application.usecases.user.JobSeekerRegistrationUseCase;
import com.lms.application.usecases.user.UsernameValidationUseCase;
import com.lms.domain.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 회원가입 컨트롤러
 * 
 * Clean Architecture의 Presentation Layer 구현
 * - 본인인증 API
 * - 재직자/구직자 회원가입 API
 * - 아이디 중복 검사 API
 * - 유효성 검증 및 오류 처리
 */
@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RegistrationController {
    
    private final IdentityVerificationUseCase identityVerificationUseCase;
    private final EmployeeRegistrationUseCase employeeRegistrationUseCase;
    private final JobSeekerRegistrationUseCase jobSeekerRegistrationUseCase;
    private final UsernameValidationUseCase usernameValidationUseCase;
    private final UserRepository userRepository;
    
    /**
     * 본인인증 API
     * 
     * @param request 본인인증 요청
     * @return 인증 결과
     */
    @PostMapping("/verify-identity")
    public ResponseEntity<ApiResponse<IdentityVerificationResponse>> verifyIdentity(
            @Valid @RequestBody IdentityVerificationRequest request) {
        
        log.info("본인인증 요청 - 이름: {}", request.getName());
        
        try {
            IdentityVerificationResponse response = identityVerificationUseCase.execute(request);
            
            if (response.getVerified()) {
                return ResponseEntity.ok(
                        ApiResponse.success("본인인증이 완료되었습니다", response)
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(response.getErrorMessage()));
            }
            
        } catch (Exception e) {
            log.error("본인인증 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("본인인증 서비스에 일시적인 오류가 발생했습니다"));
        }
    }
    
    /**
     * 재직자 회원가입 API
     * 
     * @param request 재직자 회원가입 요청
     * @return 가입 결과
     */
    @PostMapping("/employee")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerEmployee(
            @Valid @RequestBody EmployeeRegistrationRequest request) {
        
        log.info("재직자 회원가입 요청 - 아이디: {}, 회사: {}", 
                request.getUsername(), request.getCompanyName());
        
        try {
            RegistrationResponse response = employeeRegistrationUseCase.execute(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("재직자 회원가입이 완료되었습니다", response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(response.getErrorMessage()));
            }
            
        } catch (Exception e) {
            log.error("재직자 회원가입 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("회원가입 처리 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 구직자 회원가입 API
     * 
     * @param request 구직자 회원가입 요청
     * @return 가입 결과
     */
    @PostMapping("/job-seeker")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerJobSeeker(
            @Valid @RequestBody JobSeekerRegistrationRequest request) {
        
        log.info("구직자 회원가입 요청 - 아이디: {}, 희망직종: {}", 
                request.getUsername(), request.getDesiredField());
        
        try {
            RegistrationResponse response = jobSeekerRegistrationUseCase.execute(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("구직자 회원가입이 완료되었습니다", response));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(response.getErrorMessage()));
            }
            
        } catch (Exception e) {
            log.error("구직자 회원가입 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("회원가입 처리 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 아이디 종합 검증 API (완전한 검증)
     * 회원가입 시 최종 검증용
     * 
     * @param username 검사할 아이디
     * @return 종합 검증 결과
     */
    @GetMapping("/validate-username/{username}")
    public ResponseEntity<ApiResponse<ValidationResult>> validateUsername(
            @PathVariable String username) {
        
        log.info("아이디 종합 검증 요청 - 아이디: {}", username);
        
        try {
            ValidationResult result = usernameValidationUseCase.validateUsername(username);
            
            if (result.isValid()) {
                return ResponseEntity.ok(
                        ApiResponse.success(result.getMessage(), result)
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.error(result.getMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("아이디 종합 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이디 검증 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 실시간 아이디 검증 API (간소화된 검증)
     * 타이핑 중 실시간 피드백용
     * 
     * @param username 검사할 아이디
     * @return 실시간 검증 결과
     */
    @GetMapping("/quick-validate-username")
    public ResponseEntity<ApiResponse<ValidationResult>> quickValidateUsername(
            @RequestParam String username) {
        
        log.debug("실시간 아이디 검증 요청 - 아이디: {}", username);
        
        try {
            ValidationResult result = usernameValidationUseCase.quickValidate(username);
            
            if (result.isValid()) {
                return ResponseEntity.ok(
                        ApiResponse.success(result.getMessage(), result)
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.error(result.getMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("실시간 아이디 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이디 검증 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 아이디 추천 API
     * 중복된 아이디 입력 시 대안 제공
     * 
     * @param baseUsername 기본 아이디
     * @return 추천 아이디 목록
     */
    @GetMapping("/recommend-username")
    public ResponseEntity<ApiResponse<String[]>> recommendUsername(
            @RequestParam String baseUsername) {
        
        log.info("아이디 추천 요청 - 기본 아이디: {}", baseUsername);
        
        try {
            String[] recommendations = usernameValidationUseCase.generateRecommendations(baseUsername);
            
            return ResponseEntity.ok(
                    ApiResponse.success("추천 아이디 목록입니다", recommendations)
            );
            
        } catch (Exception e) {
            log.error("아이디 추천 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이디 추천 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 아이디 중복 검사 API (기존 호환성 유지)
     * 
     * @param username 검사할 아이디
     * @return 중복 여부
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameDuplicate(
            @PathVariable String username) {
        
        log.info("아이디 중복 검사 요청 - 아이디: {}", username);
        
        try {
            ValidationResult result = usernameValidationUseCase.validateUsername(username);
            
            if (result.getCode().equals("ERROR")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(result.getMessage()));
            }
            
            if (result.isValid()) {
                return ResponseEntity.ok(
                        ApiResponse.success(result.getMessage(), true)
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.error(result.getMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("아이디 중복 검사 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("아이디 중복 검사 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 이메일 중복 검사 API
     * 
     * @param email 검사할 이메일
     * @return 중복 여부
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(
            @RequestParam String email) {
        
        log.info("이메일 중복 검사 요청 - 이메일: {}", email);
        
        try {
            // 이메일 형식 검증
            if (!isValidEmail(email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("올바른 이메일 형식을 입력해주세요"));
            }
            
            boolean isDuplicate = userRepository.existsByEmail(email);
            
            if (isDuplicate) {
                return ResponseEntity.ok(
                        ApiResponse.error("이미 사용 중인 이메일입니다")
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.success("사용 가능한 이메일입니다", true)
                );
            }
            
        } catch (Exception e) {
            log.error("이메일 중복 검사 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("이메일 중복 검사 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 인증 토큰 유효성 검사 API
     * 
     * @param token 검사할 인증 토큰
     * @return 유효성 여부
     */
    @GetMapping("/verify-token/{token}")
    public ResponseEntity<ApiResponse<Boolean>> verifyToken(
            @PathVariable String token) {
        
        log.info("인증 토큰 유효성 검사 요청");
        
        try {
            boolean isValid = identityVerificationUseCase.validateToken(token);
            
            if (isValid) {
                return ResponseEntity.ok(
                        ApiResponse.success("유효한 인증 토큰입니다", true)
                );
            } else {
                return ResponseEntity.ok(
                        ApiResponse.error("유효하지 않거나 만료된 인증 토큰입니다")
                );
            }
            
        } catch (Exception e) {
            log.error("인증 토큰 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("토큰 검증 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 아이디 유효성 검증
     */
    private boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9]{4,20}$");
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