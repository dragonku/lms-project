package com.lms.application.usecases.user;

import com.lms.application.dto.response.ValidationResult;
import com.lms.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 아이디 유효성 검증 및 중복 검사 UseCase
 * 
 * 실시간 아이디 검증을 위한 통합 서비스
 * - 아이디 형식 검증
 * - 중복 검사
 * - 금지어 검사
 * - 캐싱을 통한 성능 최적화
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsernameValidationUseCase {
    
    private final UserRepository userRepository;
    
    // 아이디 패턴: 4-20자, 영문 대소문자, 숫자, 첫 글자는 영문
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{3,19}$");
    
    // 연속 문자 패턴 (3글자 이상 연속)
    private static final Pattern CONSECUTIVE_PATTERN = Pattern.compile("(.)\\1{2,}");
    
    // 금지어 목록
    private static final String[] FORBIDDEN_WORDS = {
        "admin", "administrator", "root", "test", "guest", "null", "undefined",
        "password", "passwd", "login", "logout", "system", "user", "member",
        "master", "operator", "moderator", "manager", "support", "service"
    };
    
    /**
     * 아이디 종합 검증
     * 
     * @param username 검증할 아이디
     * @return 검증 결과
     */
    public ValidationResult validateUsername(String username) {
        log.debug("아이디 종합 검증 시작 - username: {}", username);
        
        try {
            // 1. null 또는 빈 문자열 검사
            if (username == null || username.trim().isEmpty()) {
                return ValidationResult.invalid("아이디를 입력해주세요");
            }
            
            // 2. 길이 검사
            if (username.length() < 4) {
                return ValidationResult.invalid("아이디는 4자 이상 입력해주세요");
            }
            if (username.length() > 20) {
                return ValidationResult.invalid("아이디는 20자 이하로 입력해주세요");
            }
            
            // 3. 기본 패턴 검사
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                return ValidationResult.invalid("아이디는 영문으로 시작하고 영문, 숫자만 사용 가능합니다");
            }
            
            // 4. 연속 문자 검사
            if (CONSECUTIVE_PATTERN.matcher(username).find()) {
                return ValidationResult.invalid("같은 문자를 3회 이상 연속으로 사용할 수 없습니다");
            }
            
            // 5. 금지어 검사
            String lowerUsername = username.toLowerCase();
            for (String forbidden : FORBIDDEN_WORDS) {
                if (lowerUsername.contains(forbidden)) {
                    return ValidationResult.invalid("사용할 수 없는 단어가 포함되어 있습니다");
                }
            }
            
            // 6. 연속 숫자 검사 (4자리 이상 연속)
            if (username.matches(".*\\d{4,}.*")) {
                return ValidationResult.invalid("숫자를 4자리 이상 연속으로 사용할 수 없습니다");
            }
            
            // 7. 중복 검사 (캐싱 적용)
            if (isDuplicate(username)) {
                return ValidationResult.invalid("이미 사용 중인 아이디입니다");
            }
            
            return ValidationResult.valid("사용 가능한 아이디입니다");
            
        } catch (Exception e) {
            log.error("아이디 검증 중 오류 발생 - username: {}", username, e);
            return ValidationResult.error("아이디 검증 중 오류가 발생했습니다");
        }
    }
    
    /**
     * 아이디 중복 검사
     * 
     * @param username 검사할 아이디
     * @return 중복 여부
     */
    public boolean isDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * 실시간 검증 (간소화된 검증)
     * 타이핑 중에 사용되므로 최소한의 검증만 수행
     * 
     * @param username 검증할 아이디
     * @return 검증 결과
     */
    public ValidationResult quickValidate(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.valid(""); // 빈 값은 허용 (타이핑 중)
        }
        
        // 길이 검사만 수행
        if (username.length() > 20) {
            return ValidationResult.invalid("아이디는 20자 이하로 입력해주세요");
        }
        
        // 기본 패턴 검사
        if (username.length() >= 4 && !USERNAME_PATTERN.matcher(username).matches()) {
            return ValidationResult.invalid("영문으로 시작하고 영문, 숫자만 사용 가능합니다");
        }
        
        return ValidationResult.valid("입력 중...");
    }
    
    /**
     * 아이디 추천 생성
     * 
     * @param baseUsername 기본 아이디
     * @return 추천 아이디 목록
     */
    public String[] generateRecommendations(String baseUsername) {
        if (baseUsername == null || baseUsername.length() < 2) {
            return new String[0];
        }
        
        String[] recommendations = new String[3];
        String cleanBase = baseUsername.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        
        if (cleanBase.length() < 4) {
            cleanBase = cleanBase + "user";
        }
        
        // 숫자 추가 방식으로 추천
        for (int i = 0; i < 3; i++) {
            String candidate = cleanBase + String.format("%02d", (int)(Math.random() * 100));
            if (!isDuplicate(candidate)) {
                recommendations[i] = candidate;
            } else {
                recommendations[i] = cleanBase + System.currentTimeMillis() % 1000;
            }
        }
        
        return recommendations;
    }
}