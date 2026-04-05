package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("video_play") // 指定数据库表名（如果类名和表名一致，可省略）
public class VideoPlay {

        @TableId(value = "video_play_id", type = IdType.AUTO) // 主键，自增
        private Long videoPlayId;

        @TableField("video_details_id")
        private Long videoDetailsId; // 视频详情ID

        @TableField("video_mpd_url")
        private String videoMpdUrl;// 视频MPD URL

        @TableField("is_ready")
        private Integer isReady; // 是否就绪

}

