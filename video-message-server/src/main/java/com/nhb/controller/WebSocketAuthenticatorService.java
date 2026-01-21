package com.nhb.controller;

import com.nhb.properties.JwtProperties;
import com.nhb.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WebSocketAuthenticatorService {
   @Autowired
   private JwtProperties jwtProperties;
   @Autowired
    private JwtUtil jwtUtil;
    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(String token) throws AuthenticationException {


        return new UsernamePasswordAuthenticationToken(
                jwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token).get("userId").toString(),
                null,
                Collections.singleton((GrantedAuthority) () -> "USER") // 必须给至少一个角色
        );
    }
}