package com.nhb.service;

import com.nhb.model.vo.VideoInfoVO;

import java.util.List;

public interface VideoRecommendationsService {
    List<VideoInfoVO> getRandomVideoInfo(String num);
}
