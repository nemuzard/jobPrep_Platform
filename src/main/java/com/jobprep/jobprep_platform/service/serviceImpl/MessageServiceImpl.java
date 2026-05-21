package com.jobprep.jobprep_platform.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.mapper.MessageMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.message.MessageDTO;
import com.jobprep.jobprep_platform.model.entity.Message;
import com.jobprep.jobprep_platform.model.entity.User;
import com.jobprep.jobprep_platform.model.enums.message.MessageType;
import com.jobprep.jobprep_platform.model.vo.message.MessageVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.MessageService;
import com.jobprep.jobprep_platform.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestScopeData requestScopeData;
    
    @Override
    public Integer createMessage(MessageDTO messageDTO){
        try{
            Message message = new Message();
            BeanUtils.copyProperties(message, messageDTO);
            if (messageDTO.getContent()==null){
                message.setContent("");
            }
            return messageMapper.insert(message);
        } catch(Exception e){
            log.error("failed to create message: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ApiResponse<List<MessageVO>> getMessages(){
        Long userId = requestScopeData.getUserId();
        // get messages for the user
        List<Message> messages = messageMapper.selectByUserId(userId); 
        List<Long> senderIds = messages.stream().map(Message::getSenderId).toList();
        // convert to vo 
        Map<Long,User> userMap = userService.getUserMapByIds(senderIds);

        List<MessageVO> messageVOs = messages.stream().map(message->{
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message, messageVO);
            // sender info 
            MessageVO.Sender sender = new MessageVO.Sender();
            sender.setUserId(message.getSenderId());
            sender.setUsername(userMap.get(message.getSenderId()).getUsername());
            sender.setAvatarUrl(userMap.get(message.getSenderId()).getAvatarUrl());
            messageVO.setSender(sender);
            if(!Objects.equals(message.getType(),MessageType.SYSTEM)){
                MessageVO.Target target = new MessageVO.Target();
                target.setTargetId(message.getTargetId());
                target.setTargetType(message.getTargetType());
                //Todo - get comment/like info if needed
            }
            return messageVO;
        }).toList();
        return ApiResponse.success(messageVOs);
    }

    @Override
    public ApiResponse<EmptyVO> markAsRead(Integer messageId){
        Long userId = requestScopeData.getUserId();
        messageMapper.markAsRead(messageId, userId);
        return ApiResponse.success();
    }
    @Override
    public ApiResponse<EmptyVO> markAsReadBatch(List<Integer> messageIds){
        Long userId = requestScopeData.getUserId();
        messageMapper.markAsReadBatch(messageIds,userId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<EmptyVO> markAllAsRead(){
        Long userId = requestScopeData.getUserId();
        messageMapper.markAllAsRead(userId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<EmptyVO> deleteMessage(Integer messageId){
        Long userId = requestScopeData.getUserId();
        messageMapper.deleteMessage(messageId,userId);
        return ApiResponse.success();
    }
    @Override
    public ApiResponse<Integer> getUnreadCount(){
        Long userId = requestScopeData.getUserId();
        Integer count = messageMapper.countUnread(userId);
        return ApiResponse.success(count);
    }
       
}
