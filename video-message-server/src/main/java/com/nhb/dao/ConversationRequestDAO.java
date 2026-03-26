package com.nhb.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.mapper.ConversationRequestMapper;
import com.nhb.model.entity.ConversationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationRequestDAO extends ServiceImpl<ConversationRequestMapper, ConversationRequest> {


    public void createConversationRequest(ConversationRequest conversationRequest) {
        this.save(conversationRequest);
    }

    public ConversationRequest getConversationRequestById(Long conversationRequestId) {
        return lambdaQuery()
                .eq(ConversationRequest::getConversationRequestId, conversationRequestId)
                .one();
    }

    public ConversationRequest getConversationRequestByUserId(Long sendUserId, Long recipientUserId) {
        return lambdaQuery()
                .eq(ConversationRequest::getSendUserId, sendUserId)
                .eq(ConversationRequest::getRecipientUserId, recipientUserId)
                .one();
    }

    public void updateConversationRequest(ConversationRequest conversationRequest) {
        this.updateById(conversationRequest);
    }

    public List<ConversationRequest> getConversationRequestListByUserId(String userId) {
            return lambdaQuery()
                    .eq(ConversationRequest::getRecipientUserId, userId)
                    .eq(ConversationRequest::getIsAgreed, 0)
                    .list();
    }
}
