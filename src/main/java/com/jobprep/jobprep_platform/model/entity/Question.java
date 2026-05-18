package com.jobprep.jobprep_platform.model.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Question {
    
    private Integer questionId;
    private Integer categoryId;

    private String title;

    /*
     * difficulty level 
     * 1=easy，2=med，3=hard
     */
    private Integer difficulty;

    private String examPoint;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
