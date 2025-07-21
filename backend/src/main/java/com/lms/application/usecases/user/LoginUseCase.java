package com.lms.application.usecases.user;

import com.lms.application.dto.request.LoginRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.UserRepository;
import com.lms.infrastructure.security.JwtTokenProvider;
import com.lms.infrastructure.security.SessionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionManager sessionManager;

    public LoginUseCase(AuthenticationManager authenticationManager, 
                       UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       SessionManager sessionManager) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionManager = sessionManager;
    }

    public UserResponse execute(LoginRequest request) {
        try {
            // 입력값 검증
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new BadCredentialsException("Username cannot be empty");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new BadCredentialsException("Password cannot be empty");
            }

            // 인증 수행
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getUsername().trim().toLowerCase(), 
                        request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 사용자 조회
            User user = userRepository.findByUsername(request.getUsername().trim().toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(user.getUsername());
            
            // 세션 관리
            sessionManager.createSession(user.getUsername());

            // UserResponse에 토큰 포함하여 반환
            UserResponse userResponse = UserResponse.fromEntity(user);
            userResponse.setToken(token);

            return userResponse;
            
        } catch (Exception e) {
            // 로그인 실패 로그 (보안상 상세 정보는 기록하지 않음)
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
