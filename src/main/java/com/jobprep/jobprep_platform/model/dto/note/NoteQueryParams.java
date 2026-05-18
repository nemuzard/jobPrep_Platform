package com.jobprep.jobprep_platform.model.dto.note;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
@Data
public class NoteQueryParams {
    @Min(value=1, message ="problem id must be greater than 0")
    private Integer questionId;

    @Min(value = 1,message = "author id must be greater than 0")
    private Long authorId;

    @Min(value = 1, message ="Collection id must be greater than 0")
    private Integer collectionId;

    /**
     * sort by create time
     */
    @Pattern(
        regexp="create",
        message= "create"
    )
    private String sort;

    @Pattern(
        regexp="asc|desc",
        message="sort order must be 'asc' or 'desc'"
    )
    private String order;
    @Min(value=1,message="recent days must be greater than 0")
    @Max(value=30,message="recent days must be less than or equal to 30")
    private Integer recentDays;
    // default page number is 1, max page number is 10000
    @NotNull(message="page number cannot be null")
    @Min(value=1, message="page number must be greater than 0")
    @Max(value = 10000, message="page number must be less than or equal to 10000")
    private Integer page;
    
    // default page size is 10, max page size is 100
    @NotNull(message="page size cannot be null")
    @Min(value=1, message="page size must be greater than 0")
    @Max(value=100, message="page size must be less than or equal to 100")
    private Integer pageSize=10;

    // keyword for searching in note title and content, max length 100
    @Size(max=100, message="keyword length must be less than or equal to 100")
    private String keyword;

    
}
