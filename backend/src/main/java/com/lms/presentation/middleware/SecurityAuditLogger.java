package com.lms.presentation.middleware;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Enumeration;

@Component
public class SecurityAuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditLogger.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    public void logLoginAttempt(String username, boolean success, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        if (success) {
            securityLogger.info("LOGIN_SUCCESS - Username: {}, IP: {}, UserAgent: {}, Timestamp: {}", 
                username, clientIp, userAgent, LocalDateTime.now());
        } else {
            securityLogger.warn("LOGIN_FAILED - Username: {}, IP: {}, UserAgent: {}, Timestamp: {}", 
                username, clientIp, userAgent, LocalDateTime.now());
        }
    }

    public void logLogout(String username, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        securityLogger.info("LOGOUT - Username: {}, IP: {}, Timestamp: {}", 
            username, clientIp, LocalDateTime.now());
    }

    public void logAccessDenied(String username, String requestUri, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        securityLogger.warn("ACCESS_DENIED - Username: {}, URI: {}, IP: {}, Timestamp: {}", 
            username, requestUri, clientIp, LocalDateTime.now());
    }

    public void logSuspiciousActivity(String description, String username, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        securityLogger.error("SUSPICIOUS_ACTIVITY - Description: {}, Username: {}, IP: {}, Timestamp: {}", 
            description, username, clientIp, LocalDateTime.now());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}