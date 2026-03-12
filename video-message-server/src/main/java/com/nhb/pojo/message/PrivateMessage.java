package com.nhb.pojo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessage {
    private String messageId;
    private String conversationId;
    private String toUserId;
    private String content;
    private String messageType;

}
