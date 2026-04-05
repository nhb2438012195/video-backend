package com.nhb.controller;

import com.nhb.model.dto.InitChunkUploadDTO;
import com.nhb.model.dto.SendVideoCommentDTO;
import com.nhb.model.dto.UploadVideoDetailsDTO;
import com.nhb.model.vo.InitChunkUploadVO;
import com.nhb.exception.BusinessException;
import com.nhb.model.vo.VideoInfoVO;
import com.nhb.model.vo.VideoPlayInfoVO;
import com.nhb.properties.VideoProperties;
import com.nhb.result.PageResult;
import com.nhb.result.Result;
import com.nhb.service.CommonService;

import com.nhb.service.VideoService;
import com.nhb.util.RabbitMQUtil;
import com.nhb.util.RedisHashObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

@RestController
@RequestMapping("/video")
@Slf4j
@Validated
@Tag(name = "视频播放接口")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private CommonService commonService;

    @Operation(summary = "初始化分片上传视频,上传视频信息")
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
        log.info("开始上传分片视频:{}", chunkIndex);
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
        log.info("发送分片上传消息成功{}", fileName);

        return Result.success("上传分片成功");
    }
    @Operation(summary = "根据视频播放信息id获取视频播放信息")
    @GetMapping("/play")
    public Result<VideoPlayInfoVO> getVideoPlayInfo(@RequestParam("videoPlayId") Long videoPlayId) {
        log.info("开始获取视频播放信息:{}", videoPlayId);
        VideoPlayInfoVO videoPlayInfoVO = videoService.getVideoPlayInfo(videoPlayId);
        log.info("获取视频播放信息成功:{}", videoPlayInfoVO);
        return Result.success(videoPlayInfoVO);
    }
    @Operation(summary = "根据视频播放信息id获取视频详情")
    @GetMapping("/video/details/byVideoPlayId")
    public Result<VideoInfoVO> getVideoDetailsByVideoPlayId(@RequestParam("videoPlayId") Long videoPlayId) {
        log.info("开始获取视频详情:{}", videoPlayId);
        VideoInfoVO videoInfoVO = videoService.getVideoDetailsByVideoPlayId(videoPlayId);
        log.info("获取视频详情成功:{}", videoInfoVO);
        return Result.success(videoInfoVO);
    }
    @Operation(summary = "分页获取用户投稿视频")
    @GetMapping("/user/video/info/page")
    public Result getUserVideoInfoPage(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize,
                                       @RequestParam("userId") Long userId) {
        log.info("开始获取用户投稿视频:{},{},{}", pageNum, pageSize, userId);
        PageResult<VideoInfoVO> pageResult = videoService.getUserVideoInfoPage(pageNum, pageSize, userId);
        log.info("获取用户投稿视频成功:{}", pageResult);
        return Result.success(pageResult);

    }
    @Operation(summary = "获取用户投稿的视频的数量")
    @GetMapping("/user/video/num")
    public Result getUserVideoNum(@Valid @NotBlank(message = "用户id不能为空") @RequestParam("userId") Long userId) {
        log.info("开始获取用户投稿视频数量:{}", userId);
        Integer num = videoService.getUserVideoNum(userId);
        log.info("获取用户投稿视频数量成功:{}", num);
        return Result.success(num);
    }
    @Operation(summary = "投稿，上传视频详情，并开始审核")
    @PostMapping("/upload/video/details")
    public Result uploadVideoDetails(@Valid @RequestBody UploadVideoDetailsDTO uploadVideoDetailsDTO) {
        String userId = commonService.getUserId();
        log.info("用户{}开始投稿，上传视频详情:{}", userId,uploadVideoDetailsDTO);
        videoService.uploadVideoDetails(uploadVideoDetailsDTO, userId);
        return Result.success("投稿成功");
    }

}
