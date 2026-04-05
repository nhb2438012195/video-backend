package com.nhb.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhb.model.entity.VideoDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfoVO {


    @TableField("video_title")
    private String videoTitle; // 视频标题

    @TableField("video_author_id")
    private Long videoAuthorId; // 视频作者

    @TableField("video_length")
    private String videoLength; // 视频时长（秒）

    @TableField("video_play_volume")
    private Integer videoPlayVolume; // 播放量

    @TableField("video_barrage_volume")
    private Integer videoBarrageVolume; // 弹幕数量

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间

    @TableField("state")
    private String state; // 状态：0=审核中，1=正常，2=下架

    @TableField("video_cover")
    private String videoCover;//视频封面

    private Long  videoPlayId;


    public VideoInfoVO(VideoDetails videoDetails) {
        BeanUtils.copyProperties(videoDetails, this);
    }
}
