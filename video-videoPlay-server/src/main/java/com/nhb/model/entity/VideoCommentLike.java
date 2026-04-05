package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 视频评论点赞表 实体类
 * 对应数据库表：video_comment_like
 */
@Slf4j // 如果不需要日志，可移除该注解
@Data
@Builder
@AllArgsConstructor
@TableName("video_comment_like") // 对应数据库表名
public class VideoCommentLike {

    /**
     * 点赞用户ID
     * 数据库属性：bigint
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被点赞的评论ID
     * 数据库属性：bigint
     */
    @TableField("video_comment_id")
    private Long videoCommentId;

    /**
     * 点赞时间
     * 数据库属性：datetime
     */
    @TableField("creation_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationTime;

    /**
     * 状态：1为点赞 2为取消点赞
     * 数据库属性：int
     * 注意：数据库中state是int，但代码中建议用Integer，避免null无法封装的问题
     */
    @TableField("state")
    private Integer state;

    /**
     * 评论用户ID
     * 数据库属性：bigint
     */
    @TableField("comment_user_id")
    private Long  commentUserId;
    /**
     * 【可选构造函数】
     * 通常项目中还需要一个无参构造函数，MyBatis-Plus反射实例化时需要
     */
    public VideoCommentLike() {
        this.creationTime = LocalDateTime.now();
        this.state = 1;
    }
}