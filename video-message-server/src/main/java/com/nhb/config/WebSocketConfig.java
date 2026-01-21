package com.nhb.config;

import com.nhb.Interceptor.AuthChannelInterceptor;
import com.nhb.Interceptor.AuthChannelInterceptorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private AuthChannelInterceptor authInterceptor;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 允许跨域（开发时）
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                //.addInterceptors(authHandshakeInterceptor);
                //.withSockJS(); // 可选：兼容不支持 WebSocket 的浏览器
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅前缀
        registry.enableSimpleBroker("/chat", "/queue", "/topic");
        // 客户端发送消息前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 私信前缀
        registry.setUserDestinationPrefix("/user");
    }
    // 注册你的认证拦截器
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor); // ← 关键：拦截 STOMP 帧
    }

}