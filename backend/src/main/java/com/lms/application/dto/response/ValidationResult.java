package com.lms.application.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 유효성 검증 결과 DTO
 * 
 * 아이디, 이메일 등의 검증 결과를 표현하는 통일된 응답 형식
 */
@Getter
@Builder
public class ValidationResult {
    
    /**
     * 검증 성공 여부
     */
    private final boolean valid;
    
    /**
     * 검증 결과 메시지
     */
    private final String message;
    
    /**
     * 검증 결과 코드
     */
    private final String code;
    
    /**
     * 추가 데이터 (추천 아이디 등)
     */
    private final Object data;
    
    /**
     * 검증 성공 결과 생성
     */
    public static ValidationResult valid(String message) {
        return ValidationResult.builder()
                .valid(true)
                .message(message)
                .code("VALID")
                .build();
    }
    
    /**
     * 검증 성공 결과 생성 (데이터 포함)
     */
    public static ValidationResult valid(String message, Object data) {
        return ValidationResult.builder()
                .valid(true)
                .message(message)
                .code("VALID")
                .data(data)
                .build();
    }
    
    /**
     * 검증 실패 결과 생성
     */
    public static ValidationResult invalid(String message) {
        return ValidationResult.builder()
                .valid(false)
                .message(message)
                .code("INVALID")
                .build();
    }
    
    /**
     * 검증 실패 결과 생성 (코드 포함)
     */
    public static ValidationResult invalid(String message, String code) {
        return ValidationResult.builder()
                .valid(false)
                .message(message)
                .code(code)
                .build();
    }
    
    /**
     * 오류 결과 생성
     */
    public static ValidationResult error(String message) {
        return ValidationResult.builder()
                .valid(false)
                .message(message)
                .code("ERROR")
                .build();
    }
}