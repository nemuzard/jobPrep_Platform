package com.jobprep.jobprep_platform.task.email;

import lombok.Data;

@Data
public class EmailTask {
    private String email;
    private String code;
    private long timestamp;
    
}
