package com.jobprep.jobprep_platform.model.entity;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class Note {
    
   
    private Integer noteId;
    private Long authorId;
    private Integer questionId;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
