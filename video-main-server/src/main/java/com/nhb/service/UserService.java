package com.nhb.service;

import com.nhb.model.dto.UserInfoDTO;
import com.nhb.model.dto.UserLoginDTO;
import com.nhb.model.dto.UserRegisterDTO;
import com.nhb.model.vo.UserInfoVO;

public interface UserService {
     void hello();

    String login(UserLoginDTO userLoginDTO);

    void register(UserRegisterDTO userRegisterDTO);

    UserInfoVO getUserInfo(String username);


    UserInfoDTO getUserInfoById(Long userId);
}
