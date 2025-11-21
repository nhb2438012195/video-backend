package com.nhb.service;

import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.VO.UserConversationVO;
import com.nhb.pojo.entity.Conversation;

import java.util.List;

public interface ConversationService {
    List<Conversation> getConversationPage(UserGetConversationPageDTO userGetConversationPageDTO);

    List<UserConversationVO> getUserConversationVOList(List<Conversation> conversationPage);
}
