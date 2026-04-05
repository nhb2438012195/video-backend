package com.nhb.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.nhb.model.entity.VideoPlay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoPlayInfoVO {
    @TableId(value = "video_play_id", type = IdType.AUTO) // 主键，自增
    private Long videoPlayId;

    @TableField("details_id")
    private Long detailsId; // 视频详情ID

    @TableField("video_mpd_url")
    private String videoMpdUrl;// 视频MPD URL

    @TableField("is_ready")
    private Integer isReady; // 是否就绪

    public VideoPlayInfoVO(VideoPlay videoPlay) {
        BeanUtils.copyProperties(videoPlay, this);
    }
}
