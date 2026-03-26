package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_request")
public class ConversationRequest {

    @TableId(value = "conversation_request_id", type = IdType.AUTO)
    private Long conversationRequestId;

    @TableField("send_user_id")
    private Long sendUserId;

    @TableField("request_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime; // 或 LocalDateTime，取决于你的存储格式

    @TableField("is_agreed")
    private Integer isAgreed; // 0:未选择, 1:同意, 2:拒绝

    @TableField("recipient_user_id")
    private Long recipientUserId;
}