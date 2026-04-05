package com.nhb.service;

import com.nhb.model.dto.InitChunkUploadDTO;
import com.nhb.model.dto.UploadVideoDetailsDTO;
import com.nhb.model.vo.InitChunkUploadVO;
import com.nhb.model.context.ChunkUploadContext;
import com.nhb.model.vo.VideoInfoVO;
import com.nhb.model.vo.VideoPlayInfoVO;
import com.nhb.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface VideoService {



    InitChunkUploadVO initChunkUpload(InitChunkUploadDTO initChunkUploadDTO, String username);

    boolean checkChunkUploadPermission(String username, String uploadId, Integer chunkIndex);

    boolean checkChunkUploadFile(MultipartFile file);

    ChunkUploadContext getChunkUploadSession(String uploadId, Integer chunkIndex, String username);

    void uploadChunk(MultipartFile file, Integer chunkIndex, ChunkUploadContext chunkUploadContext) throws IOException;

    void uploadChunk(File file, Integer chunkIndex, ChunkUploadContext chunkUploadContext) throws IOException;

    void mergeChunks(ChunkUploadContext chunkUploadContext, String uploadKey);

    void saveUploadSession(String uploadKey, ChunkUploadContext chunkUploadContext);

    String saveMultipartFile(MultipartFile file) throws IOException;


    void commandChunksUploadService(String fileName, String uploadKey, Integer chunkIndex, String username);

    VideoPlayInfoVO getVideoPlayInfo(Long videoPlayId);

    PageResult<VideoInfoVO> getUserVideoInfoPage(Integer pageNum, Integer pageSize, Long userId);

    Integer getUserVideoNum(Long userId);

    void uploadVideoDetails(UploadVideoDetailsDTO uploadVideoDetailsDTO, String userId);

    VideoInfoVO getVideoDetailsByVideoPlayId(Long videoPlayId);
}
