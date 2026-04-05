package com.nhb.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.nhb.model.entity.VideoComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 视频评论分页VO
 * 如果点赞数量多且有回复，前端就主动查询2条回复，否则不提前查询回复
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoCommentPageVO {


    // 主键，推荐使用分布式ID（如雪花算法）
    private Long videoCommentId;


    private Long videoDetailsId; // 该评论所属视频的详情的id


    private Long commentAuthorId; // 评论发送者id


    private String commentContent; // 评论内容


    private LocalDateTime creationTime; // 创建时间


    private Integer likesNum; // 点赞数量


    private Integer repliesNum; // 回复数量


    private Long replyToUserId; // 被回复用户id 仅记录上一层的用户，不会通知顶层用户，决定通知哪个用户


    public VideoCommentPageVO(VideoComment videoComment) {
        BeanUtils.copyProperties(videoComment, this);
    }
}
