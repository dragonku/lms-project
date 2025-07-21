package com.lms.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.application.usecases.user.CreateUserUseCase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController TDD 테스트
 * 
 * Presentation Layer TDD 구현:
 * - API 엔드포인트 테스트
 * - 요청/응답 검증
 * - HTTP 상태 코드 검증
 * - JSON 직렬화/역직렬화 테스트
 */
@WebMvcTest(UserController.class)

@ActiveProfiles("test")
@DisplayName("사용자 컨트롤러 TDD 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("RED: POST /api/v1/users - 사용자 생성 요청시 201 Created 응답")
    void should_Return201Created_When_CreateUserRequest() throws Exception {
        // Given: 사용자 생성 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .name("Test User") // Added name field
                .role("STUDENT")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .name("Test User") // Added name field
                .role("STUDENT")
                .build();

        given(createUserUseCase.execute(any(CreateUserRequest.class))).willReturn(response);

        // When & Then: API 호출 및 응답 검증
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    @DisplayName("RED: POST /api/v1/users - 잘못된 요청시 400 Bad Request 응답")
    void should_Return400BadRequest_When_InvalidRequest() throws Exception {
        // Given: 잘못된 요청 (이메일 누락)
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .name("Test User") // Added name field
                .password("password123")
                .role("STUDENT")
                .build();

        // When & Then: 유효성 검증 실패
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("RED: POST /api/v1/users - 중복 이메일시 409 Conflict 응답")
    void should_Return409Conflict_When_DuplicateEmail() throws Exception {
        // Given: 중복 이메일 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("duplicate@example.com")
                .password("password123")
                .name("Test User")
                .role("STUDENT")
                .build();

        given(createUserUseCase.execute(any(CreateUserRequest.class)))
                .willThrow(new IllegalArgumentException("이미 존재하는 이메일입니다"));

        // When & Then: 중복 이메일 예외 처리
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다"));
    }
}