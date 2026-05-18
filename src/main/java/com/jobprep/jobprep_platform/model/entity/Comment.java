package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Comment {
    private Integer commentId;
    private Integer noteId;
    private Long authorId;

    private Integer parentId;
    // comment content 
    private String content;
    // how many liks 
    private Integer likeCount;
    // how many replies 
    private Integer replyCount;
    // comment created/updated time 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
