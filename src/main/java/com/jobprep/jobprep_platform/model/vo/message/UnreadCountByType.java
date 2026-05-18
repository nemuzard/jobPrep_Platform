package com.jobprep.jobprep_platform.model.vo.message;

import lombok.Data;

@Data
public class UnreadCountByType {
    private String type;
    private Integer count;
}
