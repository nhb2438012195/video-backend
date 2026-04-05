package com.nhb.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder

@AllArgsConstructor
@TableName("video_like")
public class VideoLike {

    @TableField("user_id")
    private Long userId;

    @TableField("video_details_id")
    private Long videoDetailsId;

    @TableField("stare")
    private Integer stare;

    @TableField("creation_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationTime;

    public VideoLike(){
        this.creationTime= LocalDateTime.now();
        this.stare=1;

    }

    public VideoLike(VideoDetails videoDetails) {
        this.videoDetailsId = videoDetails.getVideoDetailsId();
        this.creationTime = LocalDateTime.now();
        this.stare = 1;
    }
}