package com.nhb.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "点赞请求参数")
public class LikeActionDTO {
    @Schema(description = "操作对象ID（视频ID/评论ID）")
    private Long  likeActionId;
    @Schema(description = "操作类型 1为点赞，2为取消点赞")
    private Integer actionType;
}
