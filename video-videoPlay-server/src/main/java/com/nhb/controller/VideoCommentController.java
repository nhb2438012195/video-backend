package com.nhb.controller;

import com.nhb.model.dto.LikeActionDTO;
import com.nhb.model.dto.SendVideoCommentDTO;
import com.nhb.model.vo.VideoCommentPageVO;
import com.nhb.result.PageResult;
import com.nhb.result.Result;
import com.nhb.service.CommonService;
import com.nhb.service.VideoCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/video")
@Slf4j
@Tag(name = "视频评论接口")
public class VideoCommentController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private VideoCommentService videoCommentService;
    @Operation(summary = "发送视频评论或者回复评论")
    @PostMapping("/video/comment")
    public Result sendVideoComment(@RequestBody SendVideoCommentDTO comment) {
        Long userId = Long.valueOf(commonService.getUserId());
        log.info("开始发送视频评论:comment:{},userId{}", comment, userId);
        videoCommentService.sendVideoComment(comment, userId);
        log.info("发送视频评论成功");
        return Result.success("发送视频评论成功");
    }
    @Operation(summary = "删除视频评论")
    @DeleteMapping("/video/comment/{commentId}")  // 路径放评论ID，规范！
    public Result deleteVideoComment(@PathVariable Long commentId) {
        // 从上下文获取当前登录用户ID
        Long userId = Long.valueOf(commonService.getUserId());
        log.info("开始删除视频评论，commentId：{}，操作用户Id：{}", commentId, userId);

        // 调用自己写的软删除 + 权限校验方法
        videoCommentService.deleteVideoComment(commentId, userId);

        log.info("删除视频评论成功");
        return Result.success("删除视频评论成功");
    }
    @Operation(summary = "按照时间顺序分页获取视频评论")
    @GetMapping("/video/comment/page/byTime")
    public Result getVideoCommentByTime(
            @RequestParam Long videoDetailsId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("分页获取视频评论，videoDetailsId:{}, pageNum:{}, pageSize:{}",
                videoDetailsId, pageNum, pageSize);
        PageResult<VideoCommentPageVO> page = videoCommentService.getVideoCommentPageByTime(videoDetailsId, pageNum, pageSize);
        log.info("分页获取视频评论成功，结果：{}", page.getRecords().size());
        return Result.success(page);
    }
    @Operation(summary = "按照点赞数顺序分页获取视频评论")
    @GetMapping("/video/comment/page/byLike")
    public Result getVideoCommentByLike(
            @RequestParam Long videoDetailsId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("按照点赞数顺序分页获取视频评论，videoDetailsId:{}, pageNum:{}, pageSize:{}",
                videoDetailsId, pageNum, pageSize);
        PageResult<VideoCommentPageVO> page = videoCommentService.getVideoCommentPageByLike(videoDetailsId, pageNum, pageSize);
        log.info("按照点赞数顺序分页获取视频评论成功，结果：{}", page.getRecords().size());
        return Result.success(page);
    }
    @Operation(summary = "给评论点赞或者取消点赞")
    @PostMapping("/video/comment/action")
    public Result likeVideoComment(@RequestBody LikeActionDTO actionDTO) {
        log.info("给评论点赞或者取消点赞，actionDTO:{}", actionDTO);
        Long userId = Long.valueOf(commonService.getUserId());
        videoCommentService.likeVideoComment(actionDTO, userId);
        log.info("给评论点赞或者取消点赞成功");
        return Result.success("给评论点赞或者取消点赞成功");
    }
    @Operation(summary = "给视频点赞或者取消点赞")
    @PostMapping("/video/like")
    public Result likeVideo(@RequestBody  LikeActionDTO  actionDTO) {
        Long userId = Long.valueOf(commonService.getUserId());
        String uuid = UUID.randomUUID().toString();
        if (actionDTO.getActionType() == 1){
            log.info("{}:给视频{}点赞，userId:{}",uuid,actionDTO.getLikeActionId(), userId);
        }
        if (actionDTO.getActionType() == 2){
            log.info("{}:取消给视频{}点赞，userId:{}",uuid, actionDTO.getLikeActionId(), userId);
        }
        videoCommentService.likeVideo(actionDTO, userId);
        if (actionDTO.getActionType() == 1){
            log.info("{}:给视频点赞成功",uuid);
        }
        if (actionDTO.getActionType() == 2){
            log.info("{}取消给视频点赞成功",uuid);
        }
        return Result.success("给视频点赞成功");
    }

}
