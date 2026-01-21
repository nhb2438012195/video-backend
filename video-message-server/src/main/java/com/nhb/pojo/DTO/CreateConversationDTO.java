package com.nhb.pojo.DTO;

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
public class CreateConversationDTO {
    private Long recipientUserId;
    private Long sendUserId;
    private Long conversation_request_id;
}
