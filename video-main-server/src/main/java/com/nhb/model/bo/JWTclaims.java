package com.nhb.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import  java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTclaims {
    private Long userId;
    private String username;
    //创建时间
    private long created=System.currentTimeMillis();
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", this.username);
        claims.put("created", this.created);
        claims.put("userId", this.userId);
        return claims;
    }
    public JWTclaims(String username,Long userId){
        this.username=username;
        this.userId=userId;
    }
}
