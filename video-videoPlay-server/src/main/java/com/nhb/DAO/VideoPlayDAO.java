package com.nhb.DAO;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.model.entity.VideoPlay;
import com.nhb.mapper.VideoPlayMapper;
import org.springframework.stereotype.Service;

@Service
public class VideoPlayDAO extends ServiceImpl<VideoPlayMapper, VideoPlay> {

    public VideoPlay addVideo(VideoPlay build) {
        boolean result = save(build);
        if (result) {
            return build;
        } else {
            throw new RuntimeException("视频创建失败");
        }
    }


    public void updateVideoIdById(VideoPlay videoObject) {
        updateById(videoObject);
    }

    public  VideoPlay getVideoById(Long videoId) {
        return getById(videoId);
    }

}
