package com.nhb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhb.BO.JWTclaims;
import com.nhb.DAO.UserDAO;
import com.nhb.DTO.UserFollowDTO;
import com.nhb.DTO.UserFollowPageDTO;
import com.nhb.DTO.UserLoginDTO;
import com.nhb.DTO.UserRegisterDTO;
import com.nhb.api.UserServiceApi;
import com.nhb.entity.User;
import com.nhb.VO.UserInfoVO;
import com.nhb.entity.UserFollows;
import com.nhb.exception.RegisterFailedException;
import com.nhb.mapper.UserMapper;
import com.nhb.properties.JwtProperties;
import com.nhb.service.UserService;
import com.nhb.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserServiceApi {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void hello() {
        System.out.println("hello world");
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        // 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getUsername(),
                        userLoginDTO.getPassword()
                )
        );
        // 将认证信息保存在SecurityContextHolder中
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 从 authentication 中获取已认证的 UserDetails
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(),
                new JWTclaims(userLoginDTO.getUsername(), userDetailsImpl.getUser().getUserId()).getClaims());
        return token;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        if (Objects.isNull(userRegisterDTO)) {
            throw new RegisterFailedException("不能发空请求");
        }
        if (!StringUtils.hasText(userRegisterDTO.getUsername()) || !StringUtils.hasText(userRegisterDTO.getPassword())) {
            throw new RegisterFailedException("用户名或密码不能为空");
        }
        if (userRegisterDTO.getPassword().length() < 6) {
            throw new RegisterFailedException("密码长度不能小于6位");
        }
        if (userRegisterDTO.getPassword().length() > 20) {
            throw new RegisterFailedException("密码长度不能大于20位");
        }
        if (userDAO.getUserCountByUsername(userRegisterDTO.getUsername()) != 0) {
            throw new RegisterFailedException("用户已存在");
        }
        String password = passwordEncoder.encode(userRegisterDTO.getPassword());
        userDAO.register(new User(userRegisterDTO.getUsername(), password));

    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        User user = userDAO.getUserByUsername(username);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }


    @Override
    public List<UserFollowDTO> getUserFollowList(UserFollowPageDTO userFollowPageDTO) {
        List<UserFollows> userFollowsList = userDAO.getUserFollowsPageByUserId(
                userFollowPageDTO.getPage(),
                userFollowPageDTO.getPageSize(),
                userFollowPageDTO.getUserId()
                );

        List<UserFollowDTO> userFollowDTOList =userFollowsList.stream().map(userFollows -> {
            User followUser =userDAO.getUserById(userFollows.getFollowsUserId());
            return UserFollowDTO.builder()
                    .userId(userFollows.getUserId())
                    .followsUserId(userFollows.getUserId())
                    .followsName(followUser.getName())
                    .followsAvatar(followUser.getAvatar())
                    .build();
        }).collect(Collectors.toList());
        log.info("获取用户关注列表成功{}", userFollowDTOList.size());
        return userFollowDTOList;
    }


}
