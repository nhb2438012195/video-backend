package com.nhb.service;

import com.nhb.model.dto.LikeActionDTO;
import com.nhb.model.dto.SendVideoCommentDTO;
import com.nhb.model.vo.VideoCommentPageVO;
import com.nhb.result.PageResult;

public interface VideoCommentService {
    void sendVideoComment(SendVideoCommentDTO comment, Long userId);

    void deleteVideoComment(Long commentId, Long userId);

    PageResult<VideoCommentPageVO> getVideoCommentPageByTime(Long videoDetailsId, Integer pageNum, Integer pageSize);

    PageResult<VideoCommentPageVO> getVideoCommentPageByLike(Long videoDetailsId, Integer pageNum, Integer pageSize);

    void likeVideoComment(LikeActionDTO actionDTO, Long userId);

    void likeVideo(LikeActionDTO actionDTO, Long userId);
}
