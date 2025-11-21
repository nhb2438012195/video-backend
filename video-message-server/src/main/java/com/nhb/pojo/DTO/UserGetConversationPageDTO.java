package com.nhb.pojo.DTO;

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
public class UserGetConversationPageDTO {
    //用户id
    private Long userId;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
