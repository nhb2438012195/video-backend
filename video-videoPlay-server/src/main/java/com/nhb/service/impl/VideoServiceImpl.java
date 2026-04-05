package com.nhb.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nhb.DAO.VideoPlayDAO;
import com.nhb.DAO.VideoDetailsDAO;
import com.nhb.mapper.VideoDetailsMapper;
import com.nhb.model.dto.InitChunkUploadDTO;
import com.nhb.model.dto.UploadVideoDetailsDTO;
import com.nhb.model.entity.VideoDetails;
import com.nhb.model.vo.InitChunkUploadVO;
import com.nhb.model.command.ChunksUploadCommand;
import com.nhb.exception.BusinessException;
import com.nhb.model.command.VideoTranscodeCommand;
import com.nhb.model.vo.VideoInfoVO;
import com.nhb.model.vo.VideoPlayInfoVO;
import com.nhb.properties.VideoProperties;
import com.nhb.result.PageResult;
import com.nhb.service.CommonService;
import com.nhb.service.VideoService;
import com.nhb.model.context.ChunkUploadContext;
import com.nhb.util.MinIOUtil;
import com.nhb.util.RabbitMQUtil;
import com.nhb.util.RedisHashObjectUtils;
import com.nhb.util.S3Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoPlayDAO videoPlayDAO;
    @Autowired
    private MinIOUtil minIOUtil;
    @Autowired
    private VideoDetailsDAO videoDetailsDAO;
    @Autowired
    private RedisHashObjectUtils redisHashObjectUtils;
    @Autowired
    private VideoProperties videoProperties;
    @Autowired
    private S3Util s3Util;
    @Autowired
    private RabbitMQUtil rabbitMQUtil;
    @Autowired
    private CommonService commonService;
    @Autowired
    private VideoDetailsMapper videoDetailsMapper;



    @Override
    public InitChunkUploadVO initChunkUpload(InitChunkUploadDTO initChunkUploadDTO, String username) {
        // 参数校验
        if(Objects.isNull(initChunkUploadDTO.getTotalChunks()) ||
                initChunkUploadDTO.getTotalChunks()<=0 ||
                initChunkUploadDTO.getTotalChunks()>1000
        ){
            throw new BusinessException("TotalChunks参数错误:不能为null或者小于0大于1000");
        }
        // 生成上传会话
        String objectName=UUID.randomUUID()+"."+initChunkUploadDTO.getFileType().split("/")[1];
        String uploadKey = "chunkUpload."+username+"."+objectName;
        String uploadId =s3Util.initiateMultipartUpload(objectName);
        List<String> partETags = new ArrayList<>();
        for (int i = 0; i < initChunkUploadDTO.getTotalChunks(); i++) {
            partETags.add("");
        }
        ChunkUploadContext chunkUploadContext = ChunkUploadContext.builder()
                .uploadId(uploadId)
                .objectName(objectName)
                .partETags(partETags)
                .totalChunks(initChunkUploadDTO.getTotalChunks())
                .uploadedChunkCount(0)
                .isPaused(false)
                .build();
        redisHashObjectUtils.setObject(uploadKey, chunkUploadContext,videoProperties.getTimeout(), TimeUnit.MINUTES);
        s3Util.initiateMultipartUpload(objectName);
        return InitChunkUploadVO.builder()
                .uploadKey(uploadKey)
                .build();

    }

    // 检查上传会话权限
    @Override
    public boolean checkChunkUploadPermission(String username, String uploadId, Integer chunkIndex) {
        if(chunkIndex==null || chunkIndex<0){
            return false;
        }
        if(!username.equals(uploadId.split("\\.")[1])){
            return false;
        }
        return redisHashObjectUtils.exists(uploadId);
    }

    // 检查上传文件
    @Override
    public boolean checkChunkUploadFile(MultipartFile file) {
        if(file==null){
            return false;
        }
        if(file.getSize()>videoProperties.getMaxChunkSize()){
            return false;
        }
        return true;
    }

    // 获取上传会话
    @Override
    public ChunkUploadContext getChunkUploadSession(String uploadId, Integer chunkIndex, String username) {
        ChunkUploadContext chunkUploadContext = redisHashObjectUtils.getObject(uploadId, ChunkUploadContext.class);
        if(chunkUploadContext ==null){
            throw new BusinessException("上传视频失败:上传会话不存在");
        }
        return chunkUploadContext;
    }

    @Override
    public void uploadChunk(MultipartFile file, Integer chunkIndex, ChunkUploadContext chunkUploadContext) throws IOException {
        String partETag = s3Util.uploadPart(chunkUploadContext.getUploadId(), chunkIndex, file, chunkUploadContext.getObjectName());
        chunkUploadContext.getPartETags().set(chunkIndex-1, partETag);
        chunkUploadContext.setUploadedChunkCount(chunkUploadContext.getUploadedChunkCount()+1);
    }

    @Override
    public void uploadChunk(File file, Integer chunkIndex, ChunkUploadContext chunkUploadContext) throws IOException {
        String partETag = s3Util.uploadPart(chunkUploadContext.getUploadId(), chunkIndex, file, chunkUploadContext.getObjectName());
        chunkUploadContext.getPartETags().set(chunkIndex-1, partETag);
        chunkUploadContext.setUploadedChunkCount(chunkUploadContext.getUploadedChunkCount()+1);
    }

    @Override
    public void mergeChunks(ChunkUploadContext chunkUploadContext, String uploadKey) {
        //s3Util.completeMultipartUpload(chunkUploadSession.getUploadId(), chunkUploadSession.getPartETags(), chunkUploadSession.getObjectName());
        VideoTranscodeCommand videoTranscodeCommand = VideoTranscodeCommand.builder()
                .videoName(chunkUploadContext.getObjectName())
                .bucket(videoProperties.getBucket())
                .uploadKey(uploadKey)
                .build();
        rabbitMQUtil.sendJsonMessage(
                videoProperties.getExchange(),  // 1. 发送到哪个 Exchange
                videoProperties.getTranscodeRoutingKey(),           // 2. 使用什么 Routing Key
                videoTranscodeCommand                        // 3. 要发送的消息对象
        );
    }

    @Override
    public void saveUploadSession(String uploadKey, ChunkUploadContext chunkUploadContext) {
        redisHashObjectUtils.putField(uploadKey,"partETags", chunkUploadContext.getPartETags());
        redisHashObjectUtils.putField(uploadKey,"uploadedChunkCount", chunkUploadContext.getUploadedChunkCount());
    }

    @Override
    public String saveMultipartFile(MultipartFile file) throws IOException {
        String objectName = UUID.randomUUID().toString();
        commonService.saveMultipartFile(file, videoProperties.getVideoTemporaryFile(), objectName);
        return objectName;
    }


    @Override
    public void commandChunksUploadService(String fileName, String uploadKey, Integer chunkIndex, String username) {

        ChunksUploadCommand chunksUploadCommand = ChunksUploadCommand.builder()
                .fileName(fileName)
                .uploadKey(uploadKey)
                .chunkIndex(chunkIndex)
                .username(username)
                .build();
        rabbitMQUtil.sendJsonMessage(
                videoProperties.getExchange(),  // 1. 发送到哪个 Exchange
                videoProperties.getUploadRoutingKey(),           // 2. 使用什么 Routing Key
                chunksUploadCommand                        // 3. 要发送的消息对象
        );
    }

    @Override
    public VideoPlayInfoVO getVideoPlayInfo(Long videoPlayId) {
        return new VideoPlayInfoVO(videoPlayDAO.getVideoPlayInfoById(videoPlayId));
    }

    @Override
    public PageResult<VideoInfoVO> getUserVideoInfoPage(Integer pageNum, Integer pageSize, Long userId) {
        return new PageResult<> (new LambdaQueryChainWrapper<>(videoDetailsMapper)
                .eq(VideoDetails::getVideoAuthorId, userId)
                .orderByDesc(VideoDetails::getCreateTime)
                .page(new Page<>(pageNum, pageSize))
                .convert(VideoInfoVO::new));
    }

    @Override
    public Integer getUserVideoNum(Long userId) {
        return  new LambdaQueryChainWrapper<>(videoDetailsMapper)
                .eq(VideoDetails::getVideoAuthorId, userId)
                .count();
    }

    /**
     * 上传视频信息
     * @param uploadVideoDetailsDTO  上传视频信息
     * @param userId 投稿用户id
     */
    @Override
    public void uploadVideoDetails(UploadVideoDetailsDTO uploadVideoDetailsDTO, String userId) {
        if(userId== null || userId.isEmpty()){
            throw new BusinessException("投稿视频失败:上传用户id不能为空");
        }
        if(!redisHashObjectUtils.exists(uploadVideoDetailsDTO.getUploadKey())){
            throw new BusinessException("投稿视频失败:投稿视频key不存在");
        }
        ChunkUploadContext chunkUploadContext = redisHashObjectUtils.getObject(uploadVideoDetailsDTO.getUploadKey(), ChunkUploadContext.class);
        if(chunkUploadContext==null){
            throw new BusinessException("投稿视频失败:投稿视频key不存在");
        }
        if(!Objects.equals(chunkUploadContext.getTotalChunks(), chunkUploadContext.getUploadedChunkCount())){
            throw new BusinessException("投稿视频失败:投稿视频未完成上传");
        }
        if(chunkUploadContext.getVideoDetailsId()==null){
            throw new BusinessException("投稿视频失败:视频投稿上下文中缺少视频详情id");
        }
        //开始修改视频信息
        VideoDetails videoDetails =new LambdaQueryChainWrapper<>( videoDetailsMapper)
                .eq(VideoDetails::getVideoDetailsId, chunkUploadContext.getVideoDetailsId())
                .eq(VideoDetails::getVideoAuthorId, userId)
                .one();
        if(videoDetails==null){
            throw new BusinessException("投稿视频失败:视频投稿上下文中VideoDetailsId对应的视频详情不存在，或者用户id错误");
        }
        BeanUtils.copyProperties(uploadVideoDetailsDTO, videoDetails);
        //修改视频状态为正常,代表投稿成功，视频可以被播放
        videoDetails.setState("1");

        videoDetailsMapper.updateById(videoDetails);
    }

    @Override
    public VideoInfoVO getVideoDetailsByVideoPlayId(Long videoPlayId) {
        return new LambdaQueryChainWrapper<>(videoDetailsMapper)
                .eq(VideoDetails::getVideoPlayId, videoPlayId)
                .one()
                .convert(VideoInfoVO::new);
    }


}
