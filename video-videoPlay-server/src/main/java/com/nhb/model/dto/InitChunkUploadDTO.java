package com.nhb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitChunkUploadDTO {
    // 分片总数
    private Integer totalChunks;
    // 文件类型
    private String fileType;
    //md5
    private String md5;
    // 文件名
    private String fileName;
    // 文件大小
    private Long fileSize;
}
