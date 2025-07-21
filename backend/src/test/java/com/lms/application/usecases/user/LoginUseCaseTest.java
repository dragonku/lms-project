package com.lms.application.usecases.user;

import com.lms.application.dto.request.LoginRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.domain.entities.User;
import com.lms.domain.entities.User.UserType;
import com.lms.domain.repositories.UserRepository;
import com.lms.infrastructure.security.JwtTokenProvider;
import com.lms.infrastructure.security.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login UseCase 테스트")
class LoginUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private Authentication authentication;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(authenticationManager, userRepository, jwtTokenProvider, sessionManager);
    }

    @Test
    @DisplayName("성공적인 로그인 테스트")
    void execute_WithValidCredentials_ShouldReturnUserResponse() {
        // Given
        String username = "testuser";
        String password = "password123";
        String token = "jwt.token.here";
        
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        User user = User.builder()
                .username(username)
                .password("hashedPassword")
                .email("test@example.com")
                .name("Test User")
                .userType(UserType.STUDENT)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(username)).thenReturn(token);

        // When
        UserResponse result = loginUseCase.execute(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getRole()).isEqualTo("STUDENT");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(username);
        verify(jwtTokenProvider).generateToken(username);
        verify(sessionManager).createSession(username);
    }

    @Test
    @DisplayName("빈 사용자명으로 로그인 시 예외 발생 테스트")
    void execute_WithEmptyUsername_ShouldThrowBadCredentialsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password123");

        // When & Then
        assertThrows(BadCredentialsException.class, () -> loginUseCase.execute(request));
        
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("빈 비밀번호로 로그인 시 예외 발생 테스트")
    void execute_WithEmptyPassword_ShouldThrowBadCredentialsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        // When & Then
        assertThrows(BadCredentialsException.class, () -> loginUseCase.execute(request));
        
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("잘못된 인증 정보로 로그인 시 예외 발생 테스트")
    void execute_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> loginUseCase.execute(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtTokenProvider, never()).generateToken(anyString());
        verify(sessionManager, never()).createSession(anyString());
    }

    @Test
    @DisplayName("사용자명 대소문자 처리 테스트")
    void execute_WithMixedCaseUsername_ShouldNormalizeToLowercase() {
        // Given
        String username = "TestUser";
        String normalizedUsername = "testuser";
        String password = "password123";
        String token = "jwt.token.here";
        
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        User user = User.builder()
                .username(normalizedUsername)
                .password("hashedPassword")
                .email("test@example.com")
                .name("Test User")
                .userType(UserType.STUDENT)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(normalizedUsername)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(normalizedUsername)).thenReturn(token);

        // When
        UserResponse result = loginUseCase.execute(request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findByUsername(normalizedUsername);
        verify(jwtTokenProvider).generateToken(normalizedUsername);
    }
}