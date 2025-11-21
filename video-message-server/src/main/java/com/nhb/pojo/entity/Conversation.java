package com.nhb.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 聊天会话实体类 - 存储两个用户之间的聊天关系和状态
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
public class Conversation {

    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

    @TableField("user1_id")
    private Long user1Id;

    @TableField("user2_id")
    private Long user2Id;

    @TableField("last_message_content")
    private String lastMessageContent;

    @TableField("last_message_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("unread_count_for_user1")
    private Integer unreadCountForUser1;

    @TableField("unread_count_for_user2")
    private Integer unreadCountForUser2;

    // ==================== 构造方法与工具方法 ====================

    /**
     * 生成 conversation_id 的逻辑（可选）
     * 通常在业务层或 Mapper 中处理：确保 user1_id < user2_id
     */
    public static Long generateConversationId(Long userId1, Long userId2) {
        return Math.min(userId1, userId2) * 1000000L + Math.max(userId1, userId2);
    }

    /**
     * 获取当前对话中，某个用户的未读数量
     */
    public Integer getUnreadCountForUser(Long userId) {
        if (userId.equals(user1Id)) {
            return unreadCountForUser1;
        } else if (userId.equals(user2Id)) {
            return unreadCountForUser2;
        }
        return null; // 不属于此对话
    }

    /**
     * 设置某个用户的未读数量
     */
    public void setUnreadCountForUser(Long userId, Integer count) {
        if (userId.equals(user1Id)) {
            this.unreadCountForUser1 = count;
        } else if (userId.equals(user2Id)) {
            this.unreadCountForUser2 = count;
        }
    }
}