package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.time.LocalDate;
@Data
public class Statistic {
    private Integer id;
    private Integer loginCount;
    private Integer registerCount;
    private Integer totalRegisterCount;
    private Integer noteCount;
    private Integer submitNoteCount;
    private Integer totalNoteCount;
    private LocalDate date;
}
