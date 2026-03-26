package com.nhb.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.mapper.MessageMapper;
import com.nhb.model.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageDAO extends ServiceImpl<MessageMapper, Message> {
    @Autowired
    private MessageMapper messageMapper;

    public Long saveMessage(Message build) {
        messageMapper.insert(build);
        return build.getMessageId();
    }

    public List<Message> getDescMessagePageByConversationId(Long conversationId, int i, int i1) {
        Page<Message> pageParam = new Page<>(i, i1);
        return new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getMessageSendTime)
                .page(pageParam)
                .getRecords();
    }
    public List<Message> getAscMessagePageByConversationId(Long conversationId, int i, int i1) {
        Page<Message> pageParam = new Page<>(i, i1);
        return new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Message::getConversationId, conversationId)
                .orderByAsc(Message::getMessageSendTime)
                .page(pageParam)
                .getRecords();
    }
}
