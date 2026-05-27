package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.vo.message.MessageVO;
import com.jobprep.jobprep_platform.service.MessageService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ApiResponse<List<MessageVO>> getMessages() {
        return messageService.getMessages();
    }

    @GetMapping("/unread/count")
    public ApiResponse<Integer> getUnreadCount() {
        return messageService.getUnreadCount();
    }

    @PatchMapping("/{messageId}/read")
    public ApiResponse<EmptyVO> markAsRead(@PathVariable Integer messageId) {
        return messageService.markAsRead(messageId);
    }

    @PatchMapping("/batch/read")
    public ApiResponse<EmptyVO> markAsReadBatch(@RequestBody ReadMessagesRequest request) {
        return messageService.markAsReadBatch(request.getMessageIds());
    }

    @PatchMapping("/all/read")
    public ApiResponse<EmptyVO> markAllAsRead() {
        return messageService.markAllAsRead();
    }

    @DeleteMapping("/{messageId}")
    public ApiResponse<EmptyVO> deleteMessage(@PathVariable Integer messageId) {
        return messageService.deleteMessage(messageId);
    }

    @Data
    public static class ReadMessagesRequest {
        private List<Integer> messageIds;
    }
}
