package com.lms.presentation.controllers;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.ApiResponse;
import com.lms.application.dto.request.LoginRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.application.usecases.user.CreateUserUseCase;
import com.lms.application.usecases.user.LoginUseCase;
import com.lms.application.usecases.user.LogoutUseCase;
import com.lms.presentation.middleware.SecurityAuditLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CreateUserUseCase createUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SecurityAuditLogger securityAuditLogger;

    public AuthController(CreateUserUseCase createUserUseCase, 
                         LoginUseCase loginUseCase,
                         LogoutUseCase logoutUseCase,
                         SecurityAuditLogger securityAuditLogger) {
        this.createUserUseCase = createUserUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.securityAuditLogger = securityAuditLogger;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        createUserUseCase.execute(request);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully."), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest request, 
                                                              HttpServletRequest httpRequest) {
        try {
            UserResponse userResponse = loginUseCase.execute(request);
            securityAuditLogger.logLoginAttempt(request.getUsername(), true, httpRequest);
            return new ResponseEntity<>(ApiResponse.success("Login successful.", userResponse), HttpStatus.OK);
        } catch (Exception e) {
            securityAuditLogger.logLoginAttempt(request.getUsername(), false, httpRequest);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
        String username = null;
        try {
            // 현재 인증된 사용자 정보를 가져오기 (SecurityUtils 사용 예정)
            logoutUseCase.execute();
            securityAuditLogger.logLogout(username, httpRequest);
            return new ResponseEntity<>(ApiResponse.success("Logout successful."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Logout failed"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return new ResponseEntity<>(ApiResponse.success("Auth service is healthy", "OK"), HttpStatus.OK);
    }
}
