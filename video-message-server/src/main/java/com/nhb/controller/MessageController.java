package com.nhb.controller;

import com.nhb.api.UserClient;
import com.nhb.dao.ConversationDAO;
import com.nhb.dao.MessageDAO;
import com.nhb.pojo.DTO.MessagePageDTO;
import com.nhb.pojo.DTO.UserGetConversationPageDTO;
import com.nhb.pojo.VO.MessageVO;
import com.nhb.pojo.VO.UserConversationVO;
import com.nhb.pojo.entity.Conversation;
import com.nhb.result.PageResult;
import com.nhb.result.Result;
import com.nhb.service.CommonService;
import com.nhb.service.ConversationService;
import com.nhb.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@Slf4j
@Tag(name = "消息相关接口")
public class MessageController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageService messageService;
    /**
     *
     * @param userGetConversationPageDTO 用户信息用于查找关注列表
     * @return 聊天对象列表包括用户关注的用户和与这个用户最近几条聊天信息
     */
    @Operation(summary = "获取聊天对象列表")
    @GetMapping("/getChatRecipientList")
    public Result getChatRecipientList(@RequestParam UserGetConversationPageDTO userGetConversationPageDTO) {
        String userId = commonService.getUserId();
        userGetConversationPageDTO.setUserId(Long.valueOf(userId));
        //获取会话分页列表
        List<Conversation> conversationPage = conversationService.getConversationPage(userGetConversationPageDTO);
        List<UserConversationVO> userConversationVOList = conversationService.getUserConversationVOList(conversationPage);
        log.info("获取聊天对象列表成功{}", userConversationVOList.size());
        return Result.success(userConversationVOList);
    }

    @Operation(summary = "获取聊天信息分页")
    @GetMapping("/getChatMessagePage")
    public Result<List<MessageVO>> getChatMessagePage(@RequestParam MessagePageDTO messagePageDTO) {
        List<MessageVO> messageVOList = messageService.getMessageVOList(messagePageDTO);
        return Result.success(messageVOList);
    }
}
