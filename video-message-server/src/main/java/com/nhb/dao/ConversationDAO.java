package com.nhb.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.mapper.ConversationMapper;
import com.nhb.mapper.ConversationRequestMapper;
import com.nhb.pojo.entity.Conversation;
import com.nhb.pojo.entity.ConversationRequest;
import com.nhb.pojo.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationDAO extends ServiceImpl<ConversationMapper, Conversation> {
    @Autowired
    private ConversationRequestMapper conversationRequestMapper;

    public List<Conversation> getConversationPage(int page, int pageSize, Long userId) {
        Page<Conversation> pageParam = new Page<>(page, pageSize);
        LambdaQueryChainWrapper<Conversation> query = new LambdaQueryChainWrapper<>(this.baseMapper);
        return query.eq(Conversation::getUser1Id, userId)
                .or()
                .eq(Conversation::getUser2Id, userId)
                .orderByDesc(Conversation::getLastMessageTime)
                .page(pageParam)
                .getRecords();
    }


    public Conversation createConversation(Long recipientUserId, Long senderUserId, Long conversationRequestId) {
        ConversationRequest request = new LambdaQueryChainWrapper<ConversationRequest>(conversationRequestMapper)
                .eq(ConversationRequest::getConversationRequestId, conversationRequestId)
                .one();

        if (request==null) {
            throw new RuntimeException("请求不存在");
        }

        Conversation conversation = Conversation.builder()
                .user1Id(senderUserId)
                .user2Id(recipientUserId)
                .lastMessageContent("会话创建成功，开始聊天吧")
                .lastMessageTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .unreadCountForUser1(0)
                .unreadCountForUser2(0)
                .messageCount(0L)
                .build();
        this.save(conversation);
        return conversation;
    }

    public Conversation getConversationByUserId(Long sendUserId, Long recipientUserId) {
        return lambdaQuery()
                .eq(Conversation::getUser1Id, sendUserId)
                .eq(Conversation::getUser2Id, recipientUserId)
                .or()
                .eq(Conversation::getUser1Id, recipientUserId)
                .eq(Conversation::getUser2Id, sendUserId)
                .one();
    }

    public void setLastMessage(Message message, String conversationId) {
        lambdaUpdate()
                .eq(Conversation::getConversationId, conversationId)
                .set(Conversation::getLastMessageContent, message.getContent())
                .set(Conversation::getLastMessageTime, message.getMessageSendTime())
                .update();
    }
}
