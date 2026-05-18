package com.jobprep.jobprep_platform.model.vo.questionListItem;

import com.jobprep.jobprep_platform.model.vo.question.BaseQuestionVO;

import lombok.Data;

@Data
public class QuestionListItemVO {
    private Integer questionListId;
    private BaseQuestionVO question;
    private Integer rank;
}
