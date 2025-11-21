package com.nhb.api;

import com.nhb.DTO.UserFollowDTO;
import com.nhb.DTO.UserFollowPageDTO;
import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.entity.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserClient {
    @Autowired
    private UserServiceApi userService;

    public List<UserFollowDTO> getUserFollowList(UserFollowPageDTO userFollowPageDTO) {
        return userService.getUserFollowList(userFollowPageDTO);
    }


}
