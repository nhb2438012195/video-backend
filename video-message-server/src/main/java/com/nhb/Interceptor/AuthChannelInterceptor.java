package com.nhb.Interceptor;

import com.nhb.properties.JwtProperties;
import com.nhb.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;
    // Interceptor
    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization"))
                    .filter(h -> h.startsWith("Bearer "))
                    .map(h -> h.substring(7))
                    .orElseThrow(() -> new SecurityException("Missing token"));


            String userId = jwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token).get("userId").toString();
            if (userId == null) {
                log.error("连接失败");
                throw new SecurityException("Invalid or expired token");
            }
            //设置每个用户的唯一标识
            accessor.setUser(new Principal() {
                @Override public String getName() { return userId; }
            });
        }
        log.info("用户{}连接成功", Objects.requireNonNull(accessor.getUser()).getName());
        return message;
    }
}