package com.lms.application.usecases.user;

import com.lms.infrastructure.security.SecurityUtils;
import com.lms.infrastructure.security.SessionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final SessionManager sessionManager;

    public LogoutUseCase(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void execute() {
        // 현재 인증된 사용자 조회
        String username = SecurityUtils.getCurrentUsername().orElse(null);
        
        if (username != null) {
            // 세션 제거
            sessionManager.removeSession(username);
        }
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
    }
}