package com.jobprep.jobprep_platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class Pagination {
    private Integer page; // current 
    private Integer pageSize;
    private Integer total;
}
