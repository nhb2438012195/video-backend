package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("message") // 指定数据库表名（如果类名和表名一致，可省略）
public class Message {

        @TableId(value = "message_id", type = IdType.AUTO) // 主键，自增
        private Long messageId;

        @TableField(value = "conversation_id")
        private Long conversationId;

        @TableField(value = "to_user_id")
        private Long toUserId;

        @TableField("content")
        private String content;

        @TableField("message_send_time")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime messageSendTime; // 发送 时间

        @TableField("message_type")
        private String messageType;
}

