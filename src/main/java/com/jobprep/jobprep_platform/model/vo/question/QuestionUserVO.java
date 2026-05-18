package com.jobprep.jobprep_platform.model.vo.question;

import lombok.Data;
@Data
public class QuestionUserVO {
    
    private Integer questionId;
    private String title;
    private Integer difficulty;
    private String examPoint;
    private Integer viewCount;
    private UserQuestionStatus userQuestionStatus;

    @Data
    public static class UserQuestionStatus{
        private boolean finished = false;// if user finished the problem before 
        
    }
}
