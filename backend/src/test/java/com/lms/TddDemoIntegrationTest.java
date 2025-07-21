package com.lms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.application.dto.request.CreateUserRequest;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD 통합 테스트 데모
 * 
 * 전체 애플리케이션 스택 테스트:
 * - Controller → UseCase → Repository → Database
 * - TestContainers를 사용한 실제 DB 테스트
 * - TDD Red-Green-Refactor 사이클 시연
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)


@ActiveProfiles("test")
@Transactional
@DisplayName("TDD 통합 테스트 데모")
class TddDemoIntegrationTest {

    

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TDD 전체 Flow: 사용자 생성 E2E 테스트")
    void tdd_FullFlow_CreateUser_EndToEnd() throws Exception {
        // Given: TDD로 구현된 사용자 생성 요청
        CreateUserRequest request = CreateUserRequest.builder()
                .username("tdd_user")
                .email("tdd@example.com")
                .password("tddPassword123")
                .role("STUDENT")
                
                .build();

        // When & Then: 전체 스택을 통한 사용자 생성
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자가 성공적으로 생성되었습니다"))
                .andExpect(jsonPath("$.data.username").value("tdd_user"))
                .andExpect(jsonPath("$.data.email").value("tdd@example.com"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    @DisplayName("TDD 실패 케이스: 중복 이메일로 사용자 생성 시도")
    void tdd_FailureCase_DuplicateEmail() throws Exception {
        // Given: 첫 번째 사용자 생성
        CreateUserRequest firstRequest = CreateUserRequest.builder()
                .username("user1")
                .email("duplicate@example.com")
                .password("password123")
                .role("STUDENT")
                
                .build();

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // When: 동일한 이메일로 두 번째 사용자 생성 시도
        CreateUserRequest duplicateRequest = CreateUserRequest.builder()
                .username("user2")
                .email("duplicate@example.com") // 중복 이메일
                .password("password123")
                .role("STUDENT")
                
                .build();

        // Then: 409 Conflict 응답 확인
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다"));
    }
}