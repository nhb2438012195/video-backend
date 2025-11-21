package com.nhb.service;

import com.nhb.pojo.DTO.MessagePageDTO;
import com.nhb.pojo.VO.MessageVO;

import java.util.List;

public interface MessageService {
    List<MessageVO> getMessageVOList(MessagePageDTO messagePageDTO);
}
