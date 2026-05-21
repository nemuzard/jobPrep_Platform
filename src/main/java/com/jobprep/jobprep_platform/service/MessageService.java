package com.jobprep.jobprep_platform.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.message.MessageDTO;
import com.jobprep.jobprep_platform.model.vo.message.MessageVO;

@Transactional
public interface MessageService {
    
    Integer createMessage(MessageDTO messageDTO);
    ApiResponse<List<MessageVO>> getMessages();

    ApiResponse<EmptyVO> markAsRead(Integer messageId);
    ApiResponse<EmptyVO> markAsReadBatch(List<Integer> messageIds);

    ApiResponse<EmptyVO> markAllAsRead();
    ApiResponse<EmptyVO> deleteMessage(Integer messageId);
    ApiResponse<Integer> getUnreadCount();
}
