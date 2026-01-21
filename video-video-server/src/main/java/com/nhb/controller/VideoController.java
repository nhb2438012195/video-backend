package com.nhb.controller;

import com.nhb.DTO.InitChunkUploadDTO;
import com.nhb.VO.InitChunkUploadVO;
import com.nhb.exception.BusinessException;
import com.nhb.properties.VideoProperties;
import com.nhb.result.Result;
import com.nhb.service.CommonService;

import com.nhb.api.UserServiceApi;
import com.nhb.service.VideoService;
import com.nhb.util.RabbitMQUtil;
import com.nhb.util.RedisHashObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/video")
@Slf4j
@Tag(name = "视频播放接口")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoProperties videoProperties;

    @Autowired
    private RabbitMQUtil rabbitMQUtil;

    @Autowired
    private RedisHashObjectUtils redisHashObjectUtils;

    @Autowired
    private CommonService commonService;

    @Operation(summary = "初始化分片上传视频")
    @PostMapping("/initChunkUpload")
    public Result initChunkUpload(@RequestBody InitChunkUploadDTO initChunkUploadDTO) {
        //校验用户名
        log.info("开始初始化分片上传");
        String userId = commonService.getUserId();
        InitChunkUploadVO initChunkUploadVO = videoService.initChunkUpload(initChunkUploadDTO, userId);
        log.info("初始化分片上传成功:{}", initChunkUploadVO);
        return Result.success(initChunkUploadVO);
    }
    /**
     * 上传分片视频
     * @param file         分片视频
     * @param uploadKey   上传标识
     * @param chunkIndex  分片索引
     * @return 上传结果
     */
    @Operation(summary = "上传分片视频")
    @PostMapping("/chunkUpload")
    public Result chunkUpload(@RequestParam("chunk") MultipartFile file,
                             @RequestParam("uploadKey") String uploadKey,
                             @RequestParam("partNumber") Integer chunkIndex) throws IOException {
        //log.info("开始上传分片视频:{}", chunkIndex);
        String username = commonService.getUserId();
        //检查是否有权限上传
        if(!videoService.checkChunkUploadPermission(username, uploadKey,chunkIndex)){
            throw new BusinessException("上传分片视频失败:无权限上传");
        };
        //检查分片文件是否合理
        if(!videoService.checkChunkUploadFile(file)){
            throw new BusinessException("上传分片视频失败:分片文件不合法");
        }
        //把分片文件保存到本地
        log.info("保存分片文件");
        String fileName = videoService.saveMultipartFile(file);

        //发消息到MQ，进行分片上传
        log.info("发送分片上传消息");
        videoService.commandChunksUploadService(fileName, uploadKey, chunkIndex,username);

        return Result.success("上传分片成功");
    }
}
