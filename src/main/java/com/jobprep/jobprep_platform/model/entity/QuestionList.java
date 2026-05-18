package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.util.Date;
@Data

public class QuestionList {
    
    private Integer questionListId;

    private String name;

    // problem list type
    private Integer type;

    // problem list description
    private String description;

    private Date createdAt;

    private Date updatedAt;
}
