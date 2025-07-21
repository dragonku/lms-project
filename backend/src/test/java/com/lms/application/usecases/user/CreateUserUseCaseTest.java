package com.lms.application.usecases.user;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = CreateUserRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .name("Test User")
                .role("STUDENT")
                .build();
    }

    @Test
    @DisplayName("새로운 사용자를 성공적으로 생성해야 한다")
    void shouldCreateUserSuccessfully() {
        // Given
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

        User savedUserEntity = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .name(request.getName())
                .userType(User.UserType.valueOf(request.getRole()))
                .build();

        // Manually set an ID for the mocked saved user
        // This is a workaround for the fact that Lombok's @Builder does not generate a builder method for @Id fields
        // and we need to simulate the ID being set by the persistence context.
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedUserEntity, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID on mocked User entity", e);
        }

        when(userRepository.save(any(User.class))).thenReturn(savedUserEntity);

        // When
        com.lms.application.dto.response.UserResponse userResponse = createUserUseCase.execute(request);

        // Then
        assertNotNull(userResponse.getId());
        assertEquals("testuser", userResponse.getUsername());
        assertEquals("test@example.com", userResponse.getEmail());
        assertEquals("Test User", userResponse.getName());
        assertEquals("STUDENT", userResponse.getRole());

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("유효하지 않은 역할로 사용자 생성 시 IllegalArgumentException을 발생시켜야 한다")
    void shouldThrowExceptionForInvalidRole() {
        // Given
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .name("Test User")
                .role("INVALID_ROLE") // Set invalid role directly
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createUserUseCase.execute(invalidRequest);
        });

        assertEquals("Invalid role: INVALID_ROLE", exception.getMessage());
    }
}