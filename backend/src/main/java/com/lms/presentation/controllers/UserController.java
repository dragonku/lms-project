package com.lms.presentation.controllers;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.application.usecases.user.CreateUserUseCase;
import com.lms.application.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 사용자 관리 REST API 컨트롤러
 * 
 * Clean Architecture의 Presentation Layer 구현
 * - HTTP 요청/응답 처리
 * - 입력 유효성 검증
 * - Application Layer와 연동
 * 
 * TDD Green Phase: 테스트를 통과하는 최소 구현
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    /**
     * 사용자 생성 API
     * 
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        try {
            UserResponse userResponse = createUserUseCase.execute(request);
            
            ApiResponse<UserResponse> response = ApiResponse.success(
                    "사용자가 성공적으로 생성되었습니다", 
                    userResponse
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // 비즈니스 규칙 위반 (중복 이메일 등)
            ApiResponse<UserResponse> response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            // 기타 예외
            ApiResponse<UserResponse> response = ApiResponse.error("사용자 생성 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}