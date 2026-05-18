package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CommentLike {
    private Integer commentLikeId;
    private Integer commentId;
    private Long userId;
    private LocalDateTime createdAt;
}
