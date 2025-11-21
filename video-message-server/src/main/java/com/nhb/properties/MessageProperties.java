package com.nhb.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message")
@Data
public class MessageProperties {
    public String messageSaveQueue;
    public String messageSaveRoutingKey;
    public String Exchange;
}
