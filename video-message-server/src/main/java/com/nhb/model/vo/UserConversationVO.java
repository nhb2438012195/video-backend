package com.nhb.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserConversationVO {

    private String conversationId;
    //聊天对象id
    private String recipientUserId;

    private String lastMessageContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    //是否未读
    private Integer unreadCount;

    //近10条聊天记录
    private List<MessageVO> messageVOList;
    //聊天对象信息
    private ConversationUserInfoVO conversationUserInfoVO;

    private String messageCount;
}
