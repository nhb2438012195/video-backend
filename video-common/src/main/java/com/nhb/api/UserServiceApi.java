package com.nhb.api;

import com.nhb.DTO.UserFollowDTO;
import com.nhb.DTO.UserFollowPageDTO;
import com.nhb.DTO.UserInfoDTO;

import java.util.List;

public interface UserServiceApi {


    UserInfoDTO getUserInfoById(Long userId);
}
