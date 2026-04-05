package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@TableName("video_comment") // 对应数据库表名
public class VideoComment {

    @TableId(value = "video_comment_id", type = IdType.ASSIGN_ID) // 主键，推荐使用分布式ID（如雪花算法）
    private Long videoCommentId;

    @TableField("video_details_id")
    private Long videoDetailsId; // 该评论所属视频的详情的id

    @TableField("comment_author_id")
    private Long commentAuthorId; // 评论发送者id

    @TableField("comment_content")
    private String commentContent; // 评论内容

    @TableField("comment_type")
    private Integer commentType; // 评论类型 1为直接评论视频的评论，2为回复其他评论的评论

    @TableField("comment_status")
    private Integer commentStatus; // 评论状态 1为正常，2为被删除

    @TableField(value = "creation_time", fill = FieldFill.INSERT)
    private LocalDateTime creationTime; // 创建时间

    @TableField("likes_num")
    private Integer likesNum; // 点赞数量

    @TableField("replies_num")
    private Integer repliesNum; // 回复数量

    @TableField("parent_comment_id")
    private Long parentCommentId; // 该评论的父评论的id，如果为-1则无父评论

    @TableField("reply_to_user_id")
    private Long replyToUserId; // 被回复用户id 仅记录上一层的用户，不会通知顶层用户

    public VideoComment(){
        this.commentStatus = 1;
        this.creationTime = LocalDateTime.now();
        this.likesNum = 0;
        this.repliesNum = 0;
    }
}