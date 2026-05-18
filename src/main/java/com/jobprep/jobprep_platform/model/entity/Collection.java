package com.jobprep.jobprep_platform.model.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Collection {
    private Integer collectionId;
    private String name;
    private String description;
    private Long creatorId;
    private Date createdAt;
    private Date updatedAt;
}
