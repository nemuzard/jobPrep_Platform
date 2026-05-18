package com.jobprep.jobprep_platform.model.dto.statistic;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatisticQueryParam {
    @NotNull(message = "page cannot be null")
    @Min(value = 1,message = "page number must greater than 0 ")
    private Integer page;

    @NotNull(message = "pageSize cannot be null")
    @Min(value = 1, message = "page size must greater than 0")
    private Integer pageSize;
}
