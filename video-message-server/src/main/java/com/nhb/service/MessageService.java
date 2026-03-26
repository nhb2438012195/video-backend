package com.nhb.service;

import com.nhb.model.dto.MessagePageDTO;
import com.nhb.model.vo.MessageVO;

import java.util.List;

public interface MessageService {
    List<MessageVO> getMessageVOList(MessagePageDTO messagePageDTO);
}
