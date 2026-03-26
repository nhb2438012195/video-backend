package com.nhb.service.impl;

import com.nhb.api.MessageUserClient;
import com.nhb.dao.ConversationDAO;
import com.nhb.dao.ConversationRequestDAO;
import com.nhb.dao.MessageDAO;
import com.nhb.model.dto.CreateConversationDTO;
import com.nhb.model.dto.ResponseCreateConversationDTO;
import com.nhb.model.dto.UserGetConversationPageDTO;
import com.nhb.model.vo.ConversationRequestVO;
import com.nhb.model.vo.ConversationUserInfoVO;
import com.nhb.model.vo.MessageVO;
import com.nhb.model.vo.UserConversationVO;
import com.nhb.model.entity.Conversation;
import com.nhb.model.entity.ConversationRequest;
import com.nhb.model.entity.Message;
import com.nhb.service.CommonService;
import com.nhb.service.ConversationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationDAO conversationDAO;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private MessageUserClient messageUserClient;
    @Autowired
    private ConversationRequestDAO conversationRequestDAO;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Override
    public List<Conversation> getConversationPage(UserGetConversationPageDTO userGetConversationPageDTO) {
        return conversationDAO.getConversationPage(
                userGetConversationPageDTO.getPage(),
                userGetConversationPageDTO.getPageSize(),
                userGetConversationPageDTO.getUserId());
    }

    @Override
    public List<UserConversationVO> getUserConversationVOList(List<Conversation> conversationPage) {
        return conversationPage
                .stream()
                .map(conversation -> {
                    Long recipientUserId = conversation
                            .getUser1Id()
                            .equals(Long.valueOf(commonService.getUserId())) ? conversation.getUser2Id() : conversation.getUser1Id();
                    return UserConversationVO
                            .builder()
                            .messageCount(String.valueOf(conversation.getMessageCount()))
                            .conversationId(String.valueOf(conversation.getConversationId()))
                            .recipientUserId(String.valueOf(recipientUserId))
                            .lastMessageContent(conversation.getLastMessageContent())
                            .lastMessageTime(conversation.getLastMessageTime())
                            .conversationUserInfoVO(Optional.ofNullable(messageUserClient.getUserInfoById(recipientUserId))
                                    .map(userInfoDTO -> {
                                        ConversationUserInfoVO conversationUserInfoVO = new ConversationUserInfoVO();
                                        BeanUtils.copyProperties(userInfoDTO, conversationUserInfoVO);
                                        return conversationUserInfoVO;
                                    })
                                    .orElse(null)
                            )
                            .unreadCount(conversation
                                    .getUser1Id()
                                    .equals(Long.valueOf(commonService.getUserId())) ?
                                    conversation.getUnreadCountForUser1() : conversation.getUnreadCountForUser2())
                            .messageVOList(messageDAO.getAscMessagePageByConversationId(conversation
                                            .getConversationId(), 1, 100)
                                            .stream()
                                            .map(message -> MessageVO.builder()
                                            .conversationId(String.valueOf(message.getConversationId()))
                                            .messageId(String.valueOf(message.getMessageId()))
                                            .toUserId(String.valueOf(message.getToUserId()))
                                            .content(message.getContent())
                                            .messageSendTime(message.getMessageSendTime())
                                            .messageType(message.getMessageType())
                                            .build()
                                    ).collect(Collectors.toList())
                            )
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public Conversation createConversation(CreateConversationDTO createConversationDTO) {
        Conversation conversation= conversationDAO.createConversation(createConversationDTO.getRecipientUserId(),
                createConversationDTO.getSendUserId(),
                createConversationDTO.getConversation_request_id()
                );
        Message message = Message.builder()
                .conversationId(conversation.getConversationId())
                .content("会话创建成功，开始聊天吧")
                .messageSendTime(LocalDateTime.now())
                .messageType("1")
                .toUserId(createConversationDTO.getSendUserId())
                .build();
        messageDAO.saveMessage(message);
        UserConversationVO userConversationVO =new UserConversationVO();
        BeanUtils.copyProperties(conversation,userConversationVO);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(conversation.getUser1Id()),
                "/queue/updateConversation",
                userConversationVO
        );
        messagingTemplate.convertAndSendToUser(
                String.valueOf(conversation.getUser2Id()),
                "/queue/updateConversation",
                userConversationVO
        );
        return conversation;
    }

    @Override
    public void createConversationRequest(ConversationRequest conversationRequest) {
        ConversationRequest request = conversationRequestDAO.getConversationRequestByUserId(
                conversationRequest.getSendUserId(),conversationRequest.getRecipientUserId());
        if (request==null) {
            conversationRequestDAO.createConversationRequest(conversationRequest);
            return;
        }
        if(request.getIsAgreed()==0){
            throw new RuntimeException("会话请求已发送");
        }
        Conversation conversation = conversationDAO.getConversationByUserId( conversationRequest.getSendUserId(),
                conversationRequest.getRecipientUserId());
        if (conversation!=null){
            throw new RuntimeException("会话已存在");
        }
        conversationRequestDAO.updateConversationRequest(conversationRequest);

    }

    @Override
    public void checkResponseCreateConversation(ResponseCreateConversationDTO responseCreateConversationDTO) {
        ConversationRequest conversationRequest = conversationRequestDAO.getConversationRequestById(
                responseCreateConversationDTO.getConversationRequestId());
        if (conversationRequest==null) {
            throw new RuntimeException("会话请求不存在");
        }
        if (!conversationRequest.getRecipientUserId().equals(responseCreateConversationDTO.getRecipientUserId())){
            throw new RuntimeException("用户id不匹配：您没有权限响应此会话请求");
        }
        if (conversationRequest.getIsAgreed()!=0){
            throw new RuntimeException("会话请求已处理");
        }

    }


    @Override
    public void rejectConversationRequest(ResponseCreateConversationDTO responseCreateConversationDTO) {
        //静默拒绝，不通知请求者，也不删除请求记录，仅更新请求记录
        ConversationRequest conversationRequest = conversationRequestDAO.getConversationRequestById(
                responseCreateConversationDTO.getConversationRequestId());
        if (conversationRequest==null) {
            throw new RuntimeException("会话请求不存在");
        }
        if (!conversationRequest.getRecipientUserId().equals(responseCreateConversationDTO.getRecipientUserId())||
        !conversationRequest.getSendUserId().equals(responseCreateConversationDTO.getSendUserId())){
            throw new RuntimeException("用户id不匹配：您没有权限响应此会话请求");
        }
        conversationRequest.setIsAgreed(2);
        conversationRequestDAO.updateConversationRequest(conversationRequest);

    }

    @Override
    public void acceptConversationRequest(ResponseCreateConversationDTO responseCreateConversationDTO) {
        //如果同意那就创建会话
        ConversationRequest conversationRequest = conversationRequestDAO.getConversationRequestById(
                responseCreateConversationDTO.getConversationRequestId());
        if (conversationRequest==null) {
            throw new RuntimeException("会话请求不存在");
        }
        if (!conversationRequest.getRecipientUserId().equals(responseCreateConversationDTO.getRecipientUserId())||
                !conversationRequest.getSendUserId().equals(responseCreateConversationDTO.getSendUserId())){
            throw new RuntimeException("用户id不匹配：您没有权限响应此会话请求");
        }
        conversationRequest.setIsAgreed(1);
        createConversation(CreateConversationDTO.builder()
                .recipientUserId(conversationRequest.getRecipientUserId())
                .sendUserId(conversationRequest.getSendUserId())
                .conversation_request_id(conversationRequest.getConversationRequestId())
                .build()
        );
        conversationRequestDAO.updateConversationRequest(conversationRequest);
    }

    @Override
    public List<ConversationRequestVO> getConversationRequestVOList(String userId) {
        List<ConversationRequest> conversationRequestList = conversationRequestDAO.getConversationRequestListByUserId(userId);
        return conversationRequestList.stream().map(conversationRequest ->{
            ConversationRequestVO conversationRequestVO =new ConversationRequestVO();
            BeanUtils.copyProperties(conversationRequest,conversationRequestVO);
                    conversationRequestVO.setSenderName(messageUserClient.getUserInfoById(conversationRequestVO.getSendUserId()).getName());
            return conversationRequestVO;
        }
        ).collect(Collectors.toList());
    }

    @Override
    public void setLastMessage(Message message, String conversationId) {
        conversationDAO.setLastMessage(message,conversationId);
    }
}
