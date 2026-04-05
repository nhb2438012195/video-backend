package com.nhb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.mapper.VideoCommentLikeMapper;
import com.nhb.mapper.VideoCommentMapper;
import com.nhb.mapper.VideoDetailsMapper;
import com.nhb.mapper.VideoLikeMapper;
import com.nhb.model.dto.LikeActionDTO;
import com.nhb.model.dto.SendVideoCommentDTO;
import com.nhb.model.entity.VideoComment;
import com.nhb.model.entity.VideoCommentLike;
import com.nhb.model.entity.VideoDetails;
import com.nhb.model.entity.VideoLike;
import com.nhb.model.vo.VideoCommentPageVO;
import com.nhb.result.PageResult;
import com.nhb.service.VideoCommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment> implements VideoCommentService  {
    @Autowired
    private VideoCommentMapper videoCommentMapper;
    @Autowired
    private VideoCommentLikeMapper videoCommentLikeMapper;
    @Autowired
    private VideoDetailsMapper videoDetailsMapper;
    @Autowired
    private VideoLikeMapper videoLikeMapper;
    @Override
    public void sendVideoComment(SendVideoCommentDTO comment, Long userId) {
        if(Objects.equals(comment.getCommentContent(), "")){
            throw new RuntimeException("评论内容不能为空");
        }
        VideoComment videoComment = new VideoComment();
        BeanUtils.copyProperties(comment, videoComment);
        videoComment.setCommentAuthorId(userId);
        if(videoComment.getCommentType()==1){
            // 直接评论视频则被回复者是视频作者id
            videoComment.setReplyToUserId(videoComment.getCommentAuthorId());
            //父评论id设为视频详情id
            videoComment.setParentCommentId(comment.getVideoDetailsId());
        }
        this.save(videoComment);
    }

    @Override
    public void deleteVideoComment(Long commentId, Long userId) {
        if(commentId==null){
            throw new RuntimeException("评论ID不能为空");
        }
       VideoComment videoComment =  lambdaQuery()
                .eq(VideoComment::getVideoCommentId, commentId)
               .one();
        if(videoComment==null){
            throw new RuntimeException("评论不存在");
        }
        if(!Objects.equals(videoComment.getCommentAuthorId(), userId)){
            throw new RuntimeException("您没有权限删除此评论");
        }
        // 删除
        videoComment.setCommentStatus(2);
    }

    @Override
    public PageResult<VideoCommentPageVO> getVideoCommentPageByTime(Long videoDetailsId, Integer pageNum, Integer pageSize) {
        return new PageResult(
                new LambdaQueryChainWrapper<>(videoCommentMapper)
                .eq(VideoComment::getVideoDetailsId, videoDetailsId)
                .eq(VideoComment::getCommentStatus, 1)
                .orderByDesc(VideoComment::getCreationTime)
                .page(new Page<>(pageNum, pageSize)).convert(VideoCommentPageVO::new)
        );
    }

    @Override
    public PageResult<VideoCommentPageVO> getVideoCommentPageByLike(Long videoDetailsId, Integer pageNum, Integer pageSize) {
        return new PageResult(
                new LambdaQueryChainWrapper<>(videoCommentMapper)
                        .eq(VideoComment::getVideoDetailsId, videoDetailsId)
                        .eq(VideoComment::getCommentStatus, 1)
                        .orderByDesc(VideoComment::getLikesNum)
                        .page(new Page<>(pageNum, pageSize)).convert(VideoCommentPageVO::new)
        );
    }

    //TODO: 同一个用户，疯狂快速点击点赞，会同时插入多条点赞记录
    @Override
    @Transactional // 事务保证原子性
    public void likeVideoComment(LikeActionDTO actionDTO, Long userId) {

        // 1. 参数校验
        if (actionDTO.getLikeActionId()== null) {
            throw new RuntimeException("评论ID不能为空");
        }
        Integer actionType = actionDTO.getActionType();
        if (actionType == null || (actionType != 1 && actionType != 2)) {
            throw new RuntimeException("操作类型错误：1点赞，2取消");
        }

        // 2. 查询评论是否存在
        VideoComment videoComment = this.lambdaQuery()
                .eq(VideoComment::getVideoCommentId, actionDTO.getLikeActionId())
                .one();
        if (videoComment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 3. 查询用户是否已经点赞过
        VideoCommentLike existLike = videoCommentLikeMapper.selectOne(new LambdaQueryWrapper<VideoCommentLike>()
                .eq(VideoCommentLike::getVideoCommentId, actionDTO.getLikeActionId())
                .eq(VideoCommentLike::getUserId, userId)
        );

        // ==========================================
        // 逻辑：1=点赞，2=取消
        // ==========================================
        if (actionType == 1) {
            // --------------------------
            // 执行：点赞
            // --------------------------
            if (existLike != null) {
                // 已经点赞，不允许重复点赞
                return;
            }

            // 1. 插入点赞记录
            VideoCommentLike like = new VideoCommentLike();
            BeanUtils.copyProperties(actionDTO, like);
            like.setUserId(userId);
            like.setCommentUserId(videoComment.getCommentAuthorId());
            like.setState(1);
            videoCommentLikeMapper.insert(like);

            // 2. 评论点赞数 +1（原子更新，并发安全）
            lambdaUpdate()
                    .eq(VideoComment::getVideoCommentId, videoComment.getVideoCommentId())
                    .setSql("likes_num = likes_num + 1")
                    .update();

        } else {
            // --------------------------
            // 执行：取消点赞
            // --------------------------
            if (existLike == null) {
                // 没有点赞记录，无法取消
                return;
            }

            // 1. 修改点赞记录状态
            new LambdaUpdateChainWrapper<>(videoCommentLikeMapper)
                    .eq(VideoCommentLike::getVideoCommentId, actionDTO.getLikeActionId())
                    .eq(VideoCommentLike::getUserId, userId)
                    .set(VideoCommentLike::getState, 2)
                    .update();


            // 2. 点赞数 -1，且不能小于0（原子更新）
            this.lambdaUpdate()
                    .eq(VideoComment::getVideoCommentId, videoComment.getVideoCommentId())
                    .setSql("likes_num = GREATEST(likes_num - 1, 0)")
                    .update();
        }
    }

    @Override
    @Transactional
    public void likeVideo(LikeActionDTO actionDTO, Long userId) {
        //参数校验
        if (actionDTO.getLikeActionId()== null) {
            throw new RuntimeException("视频ID不能为空");
        }
        Integer actionType = actionDTO.getActionType();
        if (actionType == null || (actionType != 1 && actionType != 2)) {
            throw new RuntimeException("操作类型错误：1点赞，2取消");
        }
        // 查询视频是否存在
        VideoDetails videoDetails = new LambdaQueryChainWrapper<>( videoDetailsMapper)
                .eq(VideoDetails::getVideoDetailsId, actionDTO.getLikeActionId())
                .one();
        if (videoDetails == null) {
            throw new RuntimeException("视频不存在");
        }
        //查询用户是否存在点赞记录
        VideoLike existLike = new LambdaQueryChainWrapper<>(videoLikeMapper)
                .eq(VideoLike::getVideoDetailsId, actionDTO.getLikeActionId())
                .eq( VideoLike::getUserId, userId)
                .one();
        if(existLike== null){
            //判断是否非法操作：若不存在记录则不能是取消点赞
            if (actionType == 2) {
                throw new RuntimeException("不能在未点赞时取消点赞");
            }
            //不存在记录则创建记录
            //创建默认是点赞
            VideoLike videoLike = new VideoLike(videoDetails);
            videoLike.setUserId(userId);
            videoLikeMapper.insert(videoLike);
        }
        else {
            //存在记录则更新记录
            //判断是否重复操作
            if (Objects.equals(existLike.getStare(), actionType)) {
                throw new RuntimeException("请勿重复操作");
            }
            if (actionType == 1) {
                //点赞
                //更新点赞状态
                new LambdaUpdateChainWrapper<>(videoLikeMapper)
                        .eq(VideoLike::getVideoDetailsId, actionDTO.getLikeActionId())
                        .eq(VideoLike::getUserId, userId)
                        .set(VideoLike::getStare, 1)
                        .update();
            } else {
                //取消点赞
                //更新点赞状态
                new LambdaUpdateChainWrapper<>(videoLikeMapper)
                        .eq(VideoLike::getVideoDetailsId, actionDTO.getLikeActionId())
                        .eq(VideoLike::getUserId, userId)
                        .set(VideoLike::getStare, 2)
                        .update();
            }

        }
        // 更新视频点赞数
        if (actionType == 1) {
            //点赞
            new LambdaUpdateChainWrapper<>(videoDetailsMapper)
                    .eq(VideoDetails::getVideoDetailsId, videoDetails.getVideoDetailsId())
                    .setSql("likes_num = likes_num + 1")
                    .update();
         }
        else {
            //取消点赞
            new LambdaUpdateChainWrapper<>(videoDetailsMapper)
                    .eq(VideoDetails::getVideoDetailsId, videoDetails.getVideoDetailsId())
                    .setSql("likes_num = GREATEST(likes_num - 1, 0)")
                    .update();
        }
    }


}
