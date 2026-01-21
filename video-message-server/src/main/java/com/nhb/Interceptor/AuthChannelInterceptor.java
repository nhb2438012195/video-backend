package com.nhb.Interceptor;

import com.nhb.properties.JwtProperties;
import com.nhb.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new SecurityException("Missing Authorization header in CONNECT frame");
            }

            String token = authHeader.substring(7);
            try {
                String userId = jwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token).get("userId").toString();
                if (userId == null) {
                    throw new SecurityException("Invalid token: no userId");
                }

                accessor.setUser(new UsernamePasswordAuthenticationToken(userId,null, Collections.emptyList()));
                return message;

            } catch (Exception e) {
                log.error("STOMP CONNECT 认证失败", e);
                throw new SecurityException("Authentication failed", e);
            }
        }
        return message;
    }
}