package com.nhb.DAO;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nhb.exception.RegisterFailedException;
import com.nhb.mapper.UserFollowsMapper;
import com.nhb.mapper.UserMapper;
import com.nhb.model.entity.User;
import com.nhb.model.entity.UserFollows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDAO extends ServiceImpl<UserMapper, User> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserFollowsMapper userFollowsMapper;
    public Integer getUserCountByUsername(String username) {
       return lambdaQuery().eq(User::getUsername, username).count();
    }

    public void register(User user) {
        try {
            this.save(user);
        } catch (Exception e) {
            throw new RegisterFailedException("注册失败"+e.getMessage());
        }
    }

    public User getUserByUsername(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .one();
    }

    public User getUserById(Long id) {
        return lambdaQuery()
                .eq(User::getUserId, id)
                .one();
    }
    public Long getUserIdByUsername(String username) {
        return lambdaQuery()
                .eq(User::getUsername, username)
                .select(User::getUserId)
                .one()
                .getUserId();
    }


    public List<UserFollows> getUserFollowsPageByUserId(int pageCount, int pageSize, String userId) {
        Page<UserFollows> page = new Page<>(pageCount, pageSize);
        LambdaQueryChainWrapper<UserFollows> wrapper = new LambdaQueryChainWrapper<>(userFollowsMapper);
        wrapper.eq(UserFollows::getUserId, userId)
                .orderByDesc(UserFollows:: getLastChatTimestamp);
        IPage<UserFollows> userFollowsIPage = wrapper.page(page);
        return userFollowsIPage.getRecords();
    }
}
