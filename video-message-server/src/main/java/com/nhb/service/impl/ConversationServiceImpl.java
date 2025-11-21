package com.nhb.service.impl;

import com.nhb.dao.ConversationDAO;
import com.nhb.dao.MessageDAO;
import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.VO.MessageVO;
import com.nhb.pojo.VO.UserConversationVO;
import com.nhb.pojo.entity.Conversation;
import com.nhb.service.CommonService;
import com.nhb.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationDAO conversationDAO;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MessageDAO messageDAO;
    @Override
    public List<Conversation> getConversationPage(UserGetConversationPageDTO userGetConversationPageDTO) {
        return conversationDAO.getConversationPage(
                userGetConversationPageDTO.getPage(),
                userGetConversationPageDTO.getPageSize(),
                userGetConversationPageDTO.getUserId());
    }

    @Override
    public List<UserConversationVO> getUserConversationVOList(List<Conversation> conversationPage) {
        return conversationPage.stream().map(conversation -> {
            Long recipientUserId =conversation.getUser1Id()
                    .equals(Long.valueOf(commonService.getUserId())) ? conversation.getUser2Id() : conversation.getUser1Id();
                   return UserConversationVO.builder()
                            .conversationId(conversation.getConversationId())
                            .recipientUserId(recipientUserId)
                            .lastMessageContent(conversation.getLastMessageContent())
                            .lastMessageTime(conversation.getLastMessageTime())
                            .isUnread(conversation.getUser1Id()
                                    .equals(Long.valueOf(commonService.getUserId())) ?
                                    conversation.getUnreadCountForUser1() : conversation.getUnreadCountForUser2())
                            .messageVOList(messageDAO.getMessagePageByConversationId(conversation.getConversationId(), 1, 10)
                                    .stream().map(message -> MessageVO.builder()
                                            .messageId(message.getMessageId())
                                            .toUserId(recipientUserId)
                                            .content(message.getContent())
                                            .messageSendTime(message.getMessageSendTime())
                                            .messageType(message.getMessageType())
                                            .build()
                                    ).collect(Collectors.toList())
                            )
                            .build();
                }
        ).collect(Collectors.toList());
    }
}
