package com.nhb.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationUserInfoVO {
    private Long userId;


    private String username; // 用户名


    private String name; // 用户昵称


    private String avatar; // 头像 URL
}
