package com.jobprep.jobprep_platform.model.dto.message;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MessageQueryParams {

    private String type;

    private Boolean isRead;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 1, message = "Page number must greater than 0 ")
    private Integer page = 1;

    @Min(value = 1, message = "page size must greater than 0")
    private Integer pageSize = 10;

    private String sortField = "created_at";
    
    private String sortOrder = "desc";
}
