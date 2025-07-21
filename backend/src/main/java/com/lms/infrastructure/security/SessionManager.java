package com.lms.infrastructure.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SessionManager {

    private final ConcurrentHashMap<String, Long> activeSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long sessionTimeoutMs = 30 * 60 * 1000; // 30 minutes

    public SessionManager() {
        // Clean up expired sessions every 5 minutes
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 5, 5, TimeUnit.MINUTES);
    }

    public void createSession(String username) {
        activeSessions.put(username, System.currentTimeMillis());
    }

    public void updateSessionActivity(String username) {
        if (activeSessions.containsKey(username)) {
            activeSessions.put(username, System.currentTimeMillis());
        }
    }

    public boolean isSessionValid(String username) {
        Long lastActivity = activeSessions.get(username);
        if (lastActivity == null) {
            return false;
        }
        return System.currentTimeMillis() - lastActivity < sessionTimeoutMs;
    }

    public void removeSession(String username) {
        activeSessions.remove(username);
    }

    private void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > sessionTimeoutMs);
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }
}