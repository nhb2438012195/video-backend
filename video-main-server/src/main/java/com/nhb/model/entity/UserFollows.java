package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user_follows")
public class UserFollows {

    @TableId(value = "user_follows_id", type = IdType.AUTO) // 主键，自增
    private Long userFollowsId;

    @TableField(value = "user_id")
    private Long userId; // 用户id

    @TableField("follows_user_id")
    private Long followsUserId; // 关注用户id

    @TableField("last_chat_timestamp")
    private Instant lastChatTimestamp;// 最后聊天时间

}
