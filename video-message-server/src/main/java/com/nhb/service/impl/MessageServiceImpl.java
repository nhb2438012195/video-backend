package com.nhb.service.impl;

import com.nhb.dao.MessageDAO;
import com.nhb.pojo.DTO.MessagePageDTO;
import com.nhb.pojo.VO.MessageVO;
import com.nhb.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDAO messageDAO;

    @Override
    public List<MessageVO> getMessageVOList(MessagePageDTO messagePageDTO) {
       return messageDAO.getMessagePageByConversationId(messagePageDTO.getConversationId(), messagePageDTO.getPage(), messagePageDTO.getPageSize())
                .stream().map(message -> MessageVO.builder()
                        .messageId(String.valueOf(message.getMessageId()))
                        .toUserId(String.valueOf(messagePageDTO.getRecipientUserId()))
                        .content(message.getContent())
                        .messageSendTime(message.getMessageSendTime())
                        .messageType(message.getMessageType())
                       .build()
                ).collect(Collectors.toList());
    }
}
