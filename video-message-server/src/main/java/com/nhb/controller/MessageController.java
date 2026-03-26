package com.nhb.controller;

import com.nhb.api.MessageUserClient;
import com.nhb.model.dto.MessagePageDTO;
import com.nhb.model.dto.UserGetConversationPageDTO;
import com.nhb.model.vo.ConversationRequestVO;
import com.nhb.model.vo.MessageVO;
import com.nhb.model.vo.UserConversationPageVO;
import com.nhb.model.vo.UserConversationVO;
import com.nhb.model.entity.Conversation;
import com.nhb.result.Result;
import com.nhb.service.CommonService;
import com.nhb.service.ConversationService;
import com.nhb.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/message")
@Slf4j
@Tag(name = "消息相关接口")
public class MessageController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private MessageUserClient messageUserClient;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageService messageService;

    HashMap map = new HashMap();
    /**
     *
     * @return 聊天对象列表包括用户关注的用户和与这个用户最近几条聊天信息
     */
    @Operation(summary = "获取聊天会话列表")
    @GetMapping("/conversation/list")
    public Result getConversationList(
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize) {
        String userId = commonService.getUserId();
        UserGetConversationPageDTO userGetConversationPageDTO = UserGetConversationPageDTO.builder()
                .userId(Long.valueOf(userId))
                .page(page)
                .pageSize(pageSize)
                .build();
        //获取会话分页列表
        List<Conversation> conversationPage = conversationService.getConversationPage(userGetConversationPageDTO);
        List<UserConversationVO> userConversationVOList = conversationService.getUserConversationVOList(conversationPage);
        log.info("获取聊天对象列表成功{}", userConversationVOList.size());
        return Result.success(new UserConversationPageVO(userConversationVOList));
    }

    @Operation(summary = "获取聊天信息分页")
    @GetMapping("/chatMessage/page")
    public Result<List<MessageVO>> getChatMessagePage(@RequestParam MessagePageDTO messagePageDTO) {
        List<MessageVO> messageVOList = messageService.getMessageVOList(messagePageDTO);
        return Result.success(messageVOList);
    }

//    @Operation(summary = "创建会话")
//    @PostMapping("/createConversation")
//    public Result createConversation(@RequestBody CreateConversationDTO createConversationDTO) {
//        Conversation conversation = conversationService.createConversation(createConversationDTO);
//        return Result.success("创建成功");
//    }
    @Operation(summary = "获取会话请求列表")
    @GetMapping("/conversationRequest/list")
    public Result getConversationRequestList() {
        String userId = commonService.getUserId();
        List<ConversationRequestVO> conversationRequestVOList = conversationService.getConversationRequestVOList(userId);
        return Result.success(conversationRequestVOList);
    }

}
