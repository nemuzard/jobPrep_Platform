package com.jobprep.jobprep_platform.model.entity;

import lombok.Data;
import java.util.Date;

@Data
public class NoteLike {
    private Integer noteId;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
}
