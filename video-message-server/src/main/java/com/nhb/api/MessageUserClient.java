package com.nhb.api;

import com.nhb.DTO.UserFollowDTO;
import com.nhb.DTO.UserFollowPageDTO;
import com.nhb.DTO.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserClient {
    @Autowired
    private UserServiceApi userService;

    public UserInfoDTO getUserInfoById(Long userId) {
        return userService.getUserInfoById(userId);
    }
}
