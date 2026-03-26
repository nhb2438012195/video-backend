package com.nhb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowPageDTO {
    //用户id
    private String userId;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
