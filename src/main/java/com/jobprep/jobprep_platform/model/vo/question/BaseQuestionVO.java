package com.jobprep.jobprep_platform.model.vo.question;

import lombok.Data;


@Data
public class BaseQuestionVO {
    /**
     * properties 
     */
    private Integer questionId;
    private Integer catogoryId;
    private String title;
    private Integer difficulty;
    private String examPoint;
    private Integer viewCount;


}
