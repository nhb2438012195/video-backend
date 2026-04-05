package com.nhb.service.impl;

import com.nhb.DAO.VideoPlayDAO;
import com.nhb.DAO.VideoDetailsDAO;
import com.nhb.model.entity.VideoDetails;
import com.nhb.model.vo.VideoInfoVO;
import com.nhb.service.VideoRecommendationsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VideoRecommendationsServiceImpl implements VideoRecommendationsService {
    @Autowired
    private VideoDetailsDAO videoDetailsDAO;
    @Autowired
    private VideoPlayDAO videoPlayDAO;

    @Override
    public List<VideoInfoVO> getRandomVideoInfo(String num) {
        if(Objects.isNull(num)){
            throw new RuntimeException("num参数不能为空");
        }
        int numInt = Integer.parseInt(num);
        if(numInt > 20){
            throw new RuntimeException("num参数不能大于20");
        }
       List<VideoDetails> videoDetailsList = videoDetailsDAO.getRandomVideoDetails(numInt);
        return videoDetailsList.stream().map(videoDetails -> {
                VideoInfoVO videoInfoVO =new VideoInfoVO();
                BeanUtils.copyProperties(videoDetails, videoInfoVO);
                return videoInfoVO;
                }
        ).collect(Collectors.toList());
    }
}
