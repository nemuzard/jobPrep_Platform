package com.jobprep.jobprep_platform.model.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

@Data
public class UserQueryParam {
    @Min(value=1,message="userId must be integer")
    private Long userId;

    private String account;
    @Length(max=16,message="username must within 16 characters")
    private String username;

    @Min(value = 0, message = "isAdmin, min can only be 0.")
    @Max(value = 1, message = "isAdmin, max can only be 1.")
    private Integer isAdmin;

    @Min(value = 0, message = "isBanned min =  0")
    @Max(value = 1, message = "isBanned max =  1")
    private Integer isBanned;

    @NotNull(message = "page cannot be empty")
    @Min(value = 1, message = "page must be positive integer")
    private Integer page;

    @NotNull(message = "pageSize, no value found")
    @Min(value = 0,message = "must be positive integer")
    @Max(value = 200, message = "cannot exceed 200")
    private Integer pageSize;
}
