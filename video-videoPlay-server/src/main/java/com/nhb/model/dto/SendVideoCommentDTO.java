package com.nhb.model.dto;


import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "发布评论请求参数")
public class SendVideoCommentDTO {

        @Schema(description = "视频详情ID")
        private Long videoDetailsId;

        @Schema(description = "评论内容")
        private String commentContent;

        @Schema(description = "评论类型 1-评论视频 2-回复评论")
        private Integer commentType;

        @Schema(description = "父评论ID（一级评论为0）")
        private Long parentCommentId;

        @Schema(description = "被回复用户ID（评论视频时为视频作者ID）")
        private Integer replyToUserId;

}
