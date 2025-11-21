package com.nhb.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nhb.dao.MessageDAO;
import com.nhb.pojo.VO.MessageVO;
import com.nhb.pojo.entity.Message;
import com.nhb.pojo.message.ChatMessage;
import com.nhb.pojo.message.PrivateMessage;
import com.nhb.properties.MessageProperties;
import com.nhb.properties.VideoProperties;
import com.nhb.util.RabbitMQUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@Slf4j
@Tag(name = "聊天消息接收和发送")
public class ChatController {
    @Autowired
    private MessageDAO messageDAO;

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
    public void handlePrivateMessage(PrivateMessage msg,
                                     SimpMessageHeaderAccessor headerAccessor,
                                     SimpMessagingTemplate messagingTemplate,
                                     Principal principal) {
       // String fromUserId = headerAccessor.getSessionAttributes().get("username").toString();
        // 发送给目标用户（Spring 会自动路由到其 WebSocket 连接）
        String fromUserId = principal.getName();
        Message message = Message.builder()
                .conversationId(Long.valueOf(msg.getConversationId()))
                .content(msg.getContent())
                .messageSendTime(LocalDateTime.now())
                .messageType(msg.getMessageType())
                .toUserId(Long.valueOf(msg.getToUserId()))
                .build();
       messageDAO.saveMessage(message);
        MessageVO messageVO = new MessageVO();
        BeanUtil.copyProperties(message,messageVO);
        log.info("发送私聊消息{}",messageVO.getMessageId());
        messagingTemplate.convertAndSendToUser(
            msg.getToUserId(),
            "/queue/private",
                messageVO
        );
    }
}