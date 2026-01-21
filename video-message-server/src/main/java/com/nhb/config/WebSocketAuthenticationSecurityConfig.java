package com.nhb.config;

import com.nhb.Interceptor.AuthChannelInterceptorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.core.annotation.Order;        // 注解
import org.springframework.core.Ordered;               // 接口（含 HIGHEST_PRECEDENCE）@Configuration

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {
   
    @Autowired
    private AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
   
        // 这里不用给任何东西
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
   
        registration.setInterceptors(authChannelInterceptorAdapter);
    }

}