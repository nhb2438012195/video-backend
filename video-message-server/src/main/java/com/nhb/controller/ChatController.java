package com.nhb.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nhb.api.MessageUserClient;
import com.nhb.dao.MessageDAO;
import com.nhb.model.dto.CreateConversationDTO;
import com.nhb.model.dto.ResponseCreateConversationDTO;
import com.nhb.model.vo.ConversationRequestVO;
import com.nhb.model.vo.MessageVO;
import com.nhb.model.vo.ResponseCreateConversationVO;
import com.nhb.model.entity.ConversationRequest;
import com.nhb.model.entity.Message;
import com.nhb.model.message.ChatMessage;
import com.nhb.model.message.PrivateMessage;
import com.nhb.service.ConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@Slf4j
@Tag(name = "聊天消息接收和发送")
public class ChatController {
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageUserClient messageUserClient;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 发送消息给所有人
    @MessageMapping("/chat/message")
    @SendTo("/topic/messages")
    public ChatMessage handleChatMessage(ChatMessage message,
                                         SimpMessageHeaderAccessor headerAccessor,
                                         Principal principal) {
        // 从 handshake attributes 中获取 userId
        String fromUserId = principal.getName();
        message.setFrom(fromUserId);
        return message;
    }

    // 私聊：只发给指定用户
    @MessageMapping("/chat/private")
    public void handlePrivateMessage(@Payload PrivateMessage msg,
                                     @AuthenticationPrincipal Principal principal) {
        // 发送给目标用户（Spring 会自动路由到其 WebSocket 连接）
        String askMsgId=msg.getMessageId();
        String fromUserId =principal.getName();
        Message message = Message.builder()
                .conversationId(Long.valueOf(msg.getConversationId()))
                .content(msg.getContent())
                .messageSendTime(LocalDateTime.now())
                .messageType(msg.getMessageType())
                .toUserId(Long.valueOf(msg.getToUserId()))
                .build();
        messageDAO.saveMessage(message);
        conversationService.setLastMessage(message,msg.getConversationId());
        MessageVO messageVO = new MessageVO();
        BeanUtil.copyProperties(message,messageVO);
        messageVO.setMessageId(String.valueOf(message.getMessageId()));
        log.info("发送私聊消息{}",messageVO.getMessageId());
        messagingTemplate.convertAndSendToUser(
            msg.getToUserId(),
            "/queue/private",
                messageVO
        );
        // 发送给发送方确认消息
        messageVO.setMessageType("0");
        messageVO.setContent(messageVO.getMessageId());
        messageVO.setMessageId(askMsgId);
        messagingTemplate.convertAndSendToUser(
                fromUserId,
                "/queue/private",
                messageVO
        );
    }

    // 请求与指定用户创建会话
    @MessageMapping ("chat/requestCreateConversation")
    public void requestCreateConversation(@Payload CreateConversationDTO createConversationDTO,
                                          @AuthenticationPrincipal Principal principal) {
        String fromUserId =principal.getName();
        ConversationRequest conversationRequest = ConversationRequest.builder()
                .sendUserId(Long.valueOf(fromUserId))
                .recipientUserId(createConversationDTO.getRecipientUserId())
                .requestTime(LocalDateTime.now())
                .isAgreed(0)
                .build();
        conversationService.createConversationRequest(conversationRequest);
        ConversationRequestVO conversationRequestVO = new ConversationRequestVO();
        BeanUtil.copyProperties(conversationRequest,conversationRequestVO);
        conversationRequestVO.setSenderName(messageUserClient.getUserInfoById(conversationRequestVO.getSendUserId()).getName());
        messagingTemplate.convertAndSendToUser(
                String.valueOf(createConversationDTO.getRecipientUserId()),
                "/queue/requestCreateConversation",
                conversationRequestVO
        );
    }
    //用户响应会话请求
    @MessageMapping("/chat/responseCreateConversation")
    public void responseCreateConversation(@Payload ResponseCreateConversationDTO responseCreateConversationDTO,
                                           @AuthenticationPrincipal Principal principal) {
        //发送响应的用户，也就是被请求方
        String responseUserId=principal.getName();
        responseCreateConversationDTO.setRecipientUserId(Long.valueOf(responseUserId));
        //检验响应
        conversationService.checkResponseCreateConversation(responseCreateConversationDTO);
        if(responseCreateConversationDTO.getIsAgreed()!=1){
            //拒绝
            conversationService.rejectConversationRequest(responseCreateConversationDTO);
            return;
        }
        //接受
        conversationService.acceptConversationRequest(responseCreateConversationDTO);
        ResponseCreateConversationVO responseCreateConversationVO = ResponseCreateConversationVO.builder()
                .code("1")
                .message("用户已接受请求")
                .build();
        messagingTemplate.convertAndSendToUser(
                String.valueOf(responseCreateConversationDTO.getSendUserId()),
                "/queue/responseCreateConversation",
                responseCreateConversationVO

        );
    }
}