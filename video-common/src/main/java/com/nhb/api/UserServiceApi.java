package com.nhb.api;

import com.nhb.DTO.UserFollowDTO;
import com.nhb.DTO.UserFollowPageDTO;

import java.util.List;

public interface UserServiceApi {



    List<UserFollowDTO> getUserFollowList(UserFollowPageDTO userId);

}
