package com.jobprep.jobprep_platform.model.dto.message;

import lombok.Data;

@Data
public class MessageDTO {

    private Integer messageId;

    private Long receiverId;

    private Long senderId;
    
    private Integer type;

    private Integer targetId;

    private Integer targetType;

    private String content;

    private Boolean isRead;
}
