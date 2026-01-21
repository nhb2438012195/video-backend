package com.nhb.api;

import com.nhb.DTO.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class MessageUserClient {
    @Autowired
    private UserServiceApi userService;

    public UserInfoDTO getUserInfoById(Long userId) {
        return userService.getUserInfoById(userId);
    }
}
