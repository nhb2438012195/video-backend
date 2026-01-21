package com.nhb.service;

import com.nhb.pojo.DTO.CreateConversationDTO;
import com.nhb.pojo.DTO.ResponseCreateConversationDTO;
import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.VO.ConversationRequestVO;
import com.nhb.pojo.VO.UserConversationVO;
import com.nhb.pojo.entity.Conversation;
import com.nhb.pojo.entity.ConversationRequest;
import com.nhb.pojo.entity.Message;

import java.util.List;

public interface ConversationService {
    List<Conversation> getConversationPage(UserGetConversationPageDTO userGetConversationPageDTO);

    List<UserConversationVO> getUserConversationVOList(List<Conversation> conversationPage);

    Conversation createConversation(CreateConversationDTO createConversationDTO);

    void createConversationRequest(ConversationRequest conversationRequest);

    void checkResponseCreateConversation(ResponseCreateConversationDTO responseCreateConversationDTO);


    void rejectConversationRequest(ResponseCreateConversationDTO responseCreateConversationDTO);

    void acceptConversationRequest(ResponseCreateConversationDTO responseCreateConversationDTO);

    List<ConversationRequestVO> getConversationRequestVOList(String userId);

    void setLastMessage(Message message, String conversationId);
}
