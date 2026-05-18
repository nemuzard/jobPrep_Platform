package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.util.Date;
@Data
public class QuestionListItem {

     private Integer questionListId;

    private Integer questionId;

    /*
     * start from 1 
     */
    private Integer rank;

    private Date createdAt;

    private Date updatedAt;

}
