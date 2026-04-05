package com.nhb.service.consumer;


import com.nhb.DAO.VideoPlayDAO;
import com.nhb.DAO.VideoDetailsDAO;
import com.nhb.model.command.VideoTranscodeCommand;
import com.nhb.model.context.ChunkUploadContext;
import com.nhb.model.entity.VideoPlay;
import com.nhb.model.entity.VideoDetails;
import com.nhb.exception.BusinessException;
import com.nhb.properties.VideoProperties;
import com.nhb.service.CommonService;
import com.nhb.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@Service
public class VideoTranscodeMessageConsumer {
    @Autowired
    private S3Util s3Util;
    @Autowired
    private RabbitMQUtil rabbitMQUtil;
    @Autowired
    private RedisHashObjectUtils redisHashObjectUtils;
    @Autowired
    private VideoPlayDAO videoPlayDAO;
    @Autowired
    private VideoDetailsDAO videoDetailsDAO;
    @Autowired
    private MinIOUtil minIOUtil;
    @Autowired
    private VideoProperties videoProperties;
    @Autowired
    private FFmpegUtils ffmpegUtils;
    @Autowired
    private CommonService commonService;
    @RabbitListener(queues = "${video.transcodeQueue}")
    public void transcode(VideoTranscodeCommand message)  {

        try {
            log.info("开始处理视频转码消息：{}", message.getVideoName());
            //合并分片
            log.info("开始合并分片：{}", message.getUploadKey());
            //获取上传会话信息
            ChunkUploadContext chunkUploadContext = redisHashObjectUtils.getObject(message.getUploadKey(), ChunkUploadContext.class);
            // 合并分片
            s3Util.completeMultipartUpload(chunkUploadContext.getUploadId(), chunkUploadContext.getPartETags(), chunkUploadContext.getObjectName());

            log.info("合并分片完成：{}", message.getUploadKey());
            log.info("开始转码：{}", message.getVideoName());
            //下载视频到本地
            minIOUtil.downloadFileToLocal(message.getVideoName(), videoProperties.getVideoTemporaryFile()+message.getVideoName());
            //转码
            Path input = Paths.get( videoProperties.getVideoTemporaryFile()+message.getVideoName());
            Path output = Paths.get(videoProperties.getVideoTemporaryFile()+message.getVideoName().replace(".mp4", "")+"/");
            // 执行转换（4秒分片）
            ffmpegUtils.convertMp4ToDash(input, output, 4);
            minIOUtil.uploadDirectory(videoProperties.getDashFileSaveBucket(), output.toString());
            VideoPlay videoObject = createVideo(message.getVideoName().replace(".mp4", ""));
            // 更新视频详情表中的视频ID
            redisHashObjectUtils.putField(message.getUploadKey(), "videoDetailsId", videoObject.getVideoDetailsId());
            //TODO：需要等审核后再修改为已就绪
            videoObject.setIsReady(1);
            videoPlayDAO.updateVideoIdById(videoObject);
            log.info("转码完成：{}", message.getVideoName());
        } catch (Exception e) {
            log.error("视频转码失败：{}", message.getVideoName(), e);
        }finally {
            try {
                Files.delete(Paths.get(videoProperties.getVideoTemporaryFile()+message.getVideoName()));
                commonService.deleteFolder(videoProperties.getVideoTemporaryFile(), message.getVideoName().replace(".mp4", ""));
            } catch (Exception e) {
                log.error("删除临时文件失败：{}", message.getVideoName(), e);
            }
        }
    }

    public VideoPlay createVideo(String videoMpdUrl) {
        VideoDetails videoDetails = videoDetailsDAO.addVideoDetails(new VideoDetails());
        VideoPlay video = videoPlayDAO.addVideo(VideoPlay.builder()
                .videoPlayId(null)
                .videoDetailsId(videoDetails.getVideoDetailsId())
                .videoMpdUrl(videoMpdUrl)
                .build()
        );
        if(video.getVideoPlayId()==null){
            throw new BusinessException("视频创建失败");
        }
        // 更新视频详情表中的视频ID
        videoDetails.setVideoPlayId(video.getVideoPlayId());

        videoDetailsDAO.updateVideoDetailsIdById(videoDetails);
        return video;
    }
}
