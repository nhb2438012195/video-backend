package com.nhb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nhb.model.entity.VideoComment;
import com.nhb.model.entity.VideoCommentLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoCommentLikeMapper  extends BaseMapper<VideoCommentLike> {
}
