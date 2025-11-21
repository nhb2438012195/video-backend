package com.nhb.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatRecipientVO {//这是在聊天页面获取关注列表后转换成聊天对象
    //    聊天对象id
    private Long RecipientUserId;
    //    聊天对象名称
    private String RecipientName;
    //    聊天对象头像
    private String RecipientAvatar;
    //    聊天对象消息列表
    private List<MessageVO> messageVOList;
}
