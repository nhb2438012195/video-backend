// UserSessionRegistry.java
package com.nhb.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {

    // userId -> Set<session>
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(WebSocketSession session) {
        String userId = getSessionUserId(session);
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
    }

    public Set<WebSocketSession> getSessions(String userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    public boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId) && !userSessions.get(userId).isEmpty();
    }

    // 从 session attributes 中获取 userId
    public String getSessionUserId(WebSocketSession session) {
        return (String) session.getAttributes().get("CURRENT_USER_ID");
    }
}