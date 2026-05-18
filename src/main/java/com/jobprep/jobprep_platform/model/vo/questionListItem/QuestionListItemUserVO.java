package com.jobprep.jobprep_platform.model.vo.questionListItem;

import lombok.Data;
import com.jobprep.jobprep_platform.model.vo.question.BaseQuestionVO;
@Data
public class QuestionListItemUserVO {
    private Integer questionListId;
    private BaseQuestionVO question;
    private UserQuestionStatus userQuestionStatus;
    private Integer rank;
    
    @Data
    public static class UserQuestionStatus{
        private boolean finished;
    }
}
