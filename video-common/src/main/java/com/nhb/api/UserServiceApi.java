package com.nhb.api;


import com.nhb.model.dto.UserInfoDTO;

public interface UserServiceApi {


    UserInfoDTO getUserInfoById(Long userId);
}
