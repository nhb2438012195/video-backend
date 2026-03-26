package com.nhb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCreateConversationDTO {

    private Long conversationRequestId;

    //是否同意
    private Integer isAgreed;

    private Long sendUserId;

    private Long recipientUserId;
}
