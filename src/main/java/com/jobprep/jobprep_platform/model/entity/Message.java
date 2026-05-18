package com.jobprep.jobprep_platform.model.entity;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class Message {

    private Integer messageId;

    private Long receiverId;

    private Long senderId;

    private Integer type;

    private Integer targetId;

    
    private Integer targetType;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
