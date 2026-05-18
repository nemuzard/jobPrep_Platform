package com.jobprep.jobprep_platform.model.entity;

import java.util.Date;

import lombok.Data;

@Data
public class CollectionNote {
    private Integer collectionId;
    private Integer noteId;
    private Date createdAt;
    private Date updatedAt;
    
}
