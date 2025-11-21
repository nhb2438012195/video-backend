package com.nhb.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.mapper.ConversationMapper;
import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.entity.Conversation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationDAO extends ServiceImpl<ConversationMapper, Conversation> {

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
}
