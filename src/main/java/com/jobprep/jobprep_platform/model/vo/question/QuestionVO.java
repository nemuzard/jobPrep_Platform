package com.jobprep.jobprep_platform.model.vo.question;

import java.time.LocalDateTime;

import lombok.Data;

// for admin managing questions in batch
@Data
public class QuestionVO {
    
    private Integer questionId;
    private Integer categoryId;
    private String title;
    private Integer difficulty;
    private String examPoint;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
