package com.nhb.service;

import com.nhb.model.dto.CreateConversationDTO;
import com.nhb.model.dto.ResponseCreateConversationDTO;
import com.nhb.model.dto.UserGetConversationPageDTO;
import com.nhb.model.vo.ConversationRequestVO;
import com.nhb.model.vo.UserConversationVO;
import com.nhb.model.entity.Conversation;
import com.nhb.model.entity.ConversationRequest;
import com.nhb.model.entity.Message;

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
