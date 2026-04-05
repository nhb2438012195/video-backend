package com.nhb.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhb.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadVideoDetailsDTO {

    @NotBlank(message = "投稿视频失败：视频标题不能为空")
    private String videoTitle; // 视频标题

    @NotBlank(message = "投稿视频失败：视频描述不能为空")
    private String videoDescription; // 视频描述

    @NotBlank(message = "投稿视频失败：视频封面不能为空")
    private String videoCover;//视频封面

    @NotBlank(message = "投稿视频失败：视频key不能为空")
    private String uploadKey;//视频key，表示上传的是哪个视频

    @NotBlank(message = "投稿视频失败：视频md5不能为空")
    private String videoMd5;  //md5

}
