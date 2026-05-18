package com.jobprep.jobprep_platform.model.enums.questionList;

import lombok.Getter;

@Getter
public enum QuestionListType {
    COMMON(1,"Normal List"),
    TRAINING_CAMP(2,"Training camp question list");
    private final Integer type;
    private final String desc;
    QuestionListType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
