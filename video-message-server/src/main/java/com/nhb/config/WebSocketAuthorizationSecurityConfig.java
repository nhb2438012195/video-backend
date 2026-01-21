package com.nhb.config;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {

        // 添加自己的映射
        messages.anyMessage().authenticated();
    }

    // 这里请自己按需求修改
    @Override
    protected boolean sameOriginDisabled() {

        return true;
    }
}
