package com.nhb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowDTO {
    private Long userId;
    private Long followsUserId;
    private String followsName;
    private String followsAvatar;
}
