package com.nhb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessagePageDTO {
    //会话id
    private Long conversationId;

    private Long RecipientUserId;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
