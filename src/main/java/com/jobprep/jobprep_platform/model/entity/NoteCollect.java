package com.jobprep.jobprep_platform.model.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NoteCollect {
    private Integer collectId;
    private Integer nodeId;
    private Long userId;
    private LocalDateTime createdAt;
}
