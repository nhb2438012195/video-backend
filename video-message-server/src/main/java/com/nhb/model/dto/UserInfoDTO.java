package com.nhb.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Long userId;


    private String username; // 用户名


    private String name; // 用户昵称


    private String avatar; // 头像 URL


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间


    private String state; // 用户状态：1=正常，2=被封禁


    private Integer followQuantity; // 关注数量

    private Integer fansQuantity; // 粉丝数

    private Integer dynamicQuantity; // 动态数

    private Integer lv; // 用户等级


    private Integer experience; // 经验

    private Integer coin; // 硬币数

    private String phone; // 手机号

    private Integer vip; // 会员：1=普通，2=大会员

}
